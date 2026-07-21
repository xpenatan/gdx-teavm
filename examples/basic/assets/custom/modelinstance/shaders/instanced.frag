precision mediump float;

out vec4 fragColor;
uniform sampler2D u_texture;
in vec2 v_texCoords;

void main() {
    fragColor = texture(u_texture, v_texCoords);
}
