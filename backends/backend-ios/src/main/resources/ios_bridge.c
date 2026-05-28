#include "ios_bridge.h"

#include <OpenGLES/ES2/gl.h>
#include <stddef.h>

#if !defined(_WIN32)
#include <unistd.h>
#endif

int gdx_teavm_ios_teavm_main(int argc, char** argv);

static GLuint gdx_teavm_ios_debug_program = 0;
static GLuint gdx_teavm_ios_debug_vbo = 0;
static GLint gdx_teavm_ios_debug_position_location = -1;

static GLuint gdx_teavm_ios_compile_shader(GLenum type, const char* source) {
    GLuint shader = glCreateShader(type);
    glShaderSource(shader, 1, &source, NULL);
    glCompileShader(shader);
    return shader;
}

static void gdx_teavm_ios_init_debug_triangle(void) {
    if(gdx_teavm_ios_debug_program != 0) {
        return;
    }
    const char* vertexSource =
            "precision highp float;\n"
            "attribute vec2 a_position;\n"
            "void main() {\n"
            "    gl_Position = vec4(a_position.x, a_position.y * 8.0, 0.0, 1.0);\n"
            "}\n";
    const char* fragmentSource =
            "precision mediump float;\n"
            "void main() {\n"
            "    gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);\n"
            "}\n";
    GLuint vertexShader = gdx_teavm_ios_compile_shader(GL_VERTEX_SHADER, vertexSource);
    GLuint fragmentShader = gdx_teavm_ios_compile_shader(GL_FRAGMENT_SHADER, fragmentSource);
    GLuint program = glCreateProgram();
    glAttachShader(program, vertexShader);
    glAttachShader(program, fragmentShader);
    glBindAttribLocation(program, 0, "a_position");
    glLinkProgram(program);
    GLint linkStatus = GL_FALSE;
    glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);
    gdx_teavm_ios_debug_position_location = glGetAttribLocation(program, "a_position");
    fprintf(stderr, "gdx-teavm debug triangle program=%u link=%d a_position=%d error=0x%x\n",
            program, linkStatus, gdx_teavm_ios_debug_position_location, glGetError());
    fflush(stderr);
    glDeleteShader(vertexShader);
    glDeleteShader(fragmentShader);

    const GLfloat vertices[] = {
        -0.8f, -0.8f,
         0.8f, -0.8f,
         0.0f,  0.8f
    };
    glGenBuffers(1, &gdx_teavm_ios_debug_vbo);
    glBindBuffer(GL_ARRAY_BUFFER, gdx_teavm_ios_debug_vbo);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    gdx_teavm_ios_debug_program = program;
}

int32_t gdx_teavm_ios_start(const char* workingDirectory) {
#if !defined(_WIN32)
    if(workingDirectory != NULL && workingDirectory[0] != '\0') {
        chdir(workingDirectory);
    }
#else
    (void) workingDirectory;
#endif
    char* argv[] = { "gdx-teavm-ios", (char*) workingDirectory };
    int argc = workingDirectory != NULL && workingDirectory[0] != '\0' ? 2 : 1;
    return (int32_t)gdx_teavm_ios_teavm_main(argc, argv);
}

void gdx_teavm_ios_set_viewport(int32_t width, int32_t height) {
    glViewport(0, 0, (GLsizei)width, (GLsizei)height);
}

int32_t gdx_teavm_ios_gl_error(void) {
    return (int32_t)glGetError();
}

void gdx_teavm_ios_get_viewport(int32_t* viewport) {
    if(viewport == NULL) {
        return;
    }
    GLint values[4] = { 0, 0, 0, 0 };
    glGetIntegerv(GL_VIEWPORT, values);
    viewport[0] = (int32_t)values[0];
    viewport[1] = (int32_t)values[1];
    viewport[2] = (int32_t)values[2];
    viewport[3] = (int32_t)values[3];
}

int32_t gdx_teavm_ios_is_scissor_enabled(void) {
    return glIsEnabled(GL_SCISSOR_TEST) == GL_TRUE ? 1 : 0;
}

void gdx_teavm_ios_get_scissor_box(int32_t* box) {
    if(box == NULL) {
        return;
    }
    GLint values[4] = { 0, 0, 0, 0 };
    glGetIntegerv(GL_SCISSOR_BOX, values);
    box[0] = (int32_t)values[0];
    box[1] = (int32_t)values[1];
    box[2] = (int32_t)values[2];
    box[3] = (int32_t)values[3];
}

void gdx_teavm_ios_debug_triangle(void) {
    GLint viewport[4] = { 0, 0, 0, 0 };
    glGetIntegerv(GL_VIEWPORT, viewport);
    glDisable(GL_DEPTH_TEST);
    glDisable(GL_CULL_FACE);
    glEnable(GL_SCISSOR_TEST);
    glScissor(viewport[0], viewport[1], viewport[2], viewport[3] / 2);
    glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    glScissor(viewport[0], viewport[1] + viewport[3] / 2, viewport[2], viewport[3] - viewport[3] / 2);
    glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    glDisable(GL_SCISSOR_TEST);

    gdx_teavm_ios_init_debug_triangle();
    glUseProgram(gdx_teavm_ios_debug_program);
    glBindBuffer(GL_ARRAY_BUFFER, gdx_teavm_ios_debug_vbo);
    if(gdx_teavm_ios_debug_position_location < 0) {
        return;
    }
    GLuint positionLocation = (GLuint)gdx_teavm_ios_debug_position_location;
    glEnableVertexAttribArray(positionLocation);
    glVertexAttribPointer(positionLocation, 2, GL_FLOAT, GL_FALSE, 0, (const GLvoid*)0);
    glDrawArrays(GL_TRIANGLES, 0, 3);
    glDisableVertexAttribArray(positionLocation);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
}
