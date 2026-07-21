in vec3 a_position;
in vec2 a_texCoords0;
in mat4 i_worldTrans;

uniform mat4 u_projViewTrans;
out vec2 v_texCoords;

void main() {
    v_texCoords = a_texCoords0;
    gl_Position = u_projViewTrans * i_worldTrans * vec4(a_position, 1.0);
}
