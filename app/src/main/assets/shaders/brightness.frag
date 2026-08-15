precision mediump float;

uniform sampler2D uTexture;
uniform float uBrightness;
varying vec2 vTextureCoordinate;

void main() {
    vec4 source = texture2D(uTexture, vTextureCoordinate);
    vec3 adjusted = clamp(source.rgb + vec3(uBrightness), 0.0, 1.0);
    gl_FragColor = vec4(adjusted, source.a);
}
