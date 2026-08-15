package com.kingmotion.engine.timeline

import com.kingmotion.engine.model.*
import java.util.UUID

object TimelineEditor {
    fun addLayer(composition: Composition, layer: Layer): Composition = normalize(composition.copy(
        layers = composition.layers + layer.copy(order = composition.layers.size),
        durationUs = maxOf(composition.durationUs, layer.endTimeUs),
    ))

    fun delete(composition: Composition, id: String) = normalize(composition.copy(layers = composition.layers.filterNot { it.id == id }))

    fun duplicate(composition: Composition, id: String): Composition {
        val source = composition.layers.firstOrNull { it.id == id } ?: return composition
        return addLayer(composition, source.copy(id = UUID.randomUUID().toString(), name = "${source.name} copy", startTimeUs = source.startTimeUs + 100_000))
    }

    fun split(composition: Composition, id: String, atUs: Long): Composition {
        val source = composition.layers.firstOrNull { it.id == id } ?: return composition
        if (source.locked || atUs <= source.startTimeUs || atUs >= source.endTimeUs) return composition
        val leftDuration = atUs - source.startTimeUs
        val right = source.copy(
            id = UUID.randomUUID().toString(), name = "${source.name} (2)", startTimeUs = atUs,
            durationUs = source.durationUs - leftDuration, sourceInTimeUs = source.sourceInTimeUs + leftDuration,
        )
        return normalize(composition.copy(layers = composition.layers.map { if (it.id == id) it.copy(durationUs = leftDuration) else it } + right))
    }

    fun trimStart(composition: Composition, id: String, newStartUs: Long): Composition = update(composition, id) { layer ->
        val clamped = newStartUs.coerceIn(layer.startTimeUs, layer.endTimeUs - 1)
        val delta = clamped - layer.startTimeUs
        layer.copy(startTimeUs = clamped, durationUs = layer.durationUs - delta, sourceInTimeUs = layer.sourceInTimeUs + delta)
    }

    fun trimEnd(composition: Composition, id: String, newEndUs: Long): Composition = update(composition, id) { layer ->
        layer.copy(durationUs = (newEndUs.coerceIn(layer.startTimeUs + 1, layer.endTimeUs) - layer.startTimeUs))
    }

    fun move(composition: Composition, id: String, delta: Int): Composition {
        val ordered = composition.layers.sortedBy(Layer::order).toMutableList()
        val from = ordered.indexOfFirst { it.id == id }
        if (from < 0) return composition
        val to = (from + delta).coerceIn(0, ordered.lastIndex)
        ordered.add(to, ordered.removeAt(from))
        return composition.copy(layers = ordered.mapIndexed { index, layer -> layer.copy(order = index) })
    }

    fun update(composition: Composition, id: String, change: (Layer) -> Layer) =
        composition.copy(layers = composition.layers.map { if (it.id == id && !it.locked) change(it) else it })

    private fun normalize(composition: Composition) = composition.copy(layers = composition.layers.sortedBy(Layer::order).mapIndexed { i, it -> it.copy(order = i) })
}

class EditHistory(initial: Composition, private val capacity: Int = 50) {
    private val undo = ArrayDeque<Composition>()
    private val redo = ArrayDeque<Composition>()
    var current: Composition = initial; private set
    fun execute(next: Composition): Composition { if (next != current) { undo.addLast(current); if (undo.size > capacity) undo.removeFirst(); current = next; redo.clear() }; return current }
    fun undo(): Composition { if (undo.isNotEmpty()) { redo.addLast(current); current = undo.removeLast() }; return current }
    fun redo(): Composition { if (redo.isNotEmpty()) { undo.addLast(current); current = redo.removeLast() }; return current }
    val canUndo get() = undo.isNotEmpty()
    val canRedo get() = redo.isNotEmpty()
}
