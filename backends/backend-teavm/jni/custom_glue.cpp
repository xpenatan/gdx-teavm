#include <emscripten.h>
#include <GDX/gdx2d/gdx2d.h>

class Gdx {
    // Emscripten webidl don't support binding methods without a class so we need to create a wrapper
    public:
        static void* g2d_get_pixels(const gdx2d_pixmap* pixmap) { return (void*)pixmap->pixels; }
        static gdx2d_pixmap* g2d_load(void * void_buffer, int offset, uint32_t len) {
            unsigned char *buffer = static_cast<unsigned char*>(void_buffer);
            return gdx2d_load(buffer + offset, len);
        }
        static gdx2d_pixmap* g2d_new(uint32_t width, uint32_t height, uint32_t format) { return gdx2d_new(width, height, format); }
        static void g2d_free(const gdx2d_pixmap* pixmap) { gdx2d_free(pixmap); }
        static void g2d_set_blend(gdx2d_pixmap* pixmap, uint32_t blend) { gdx2d_set_blend(pixmap, blend); }
        static void g2d_set_scale(gdx2d_pixmap* pixmap, uint32_t scale) { gdx2d_set_scale(pixmap, scale); }
        static const char* g2d_get_failure_reason() { return gdx2d_get_failure_reason(); }
        static void g2d_clear(const gdx2d_pixmap* pixmap, uint32_t col) { gdx2d_clear(pixmap, col); }
        static void g2d_set_pixel(const gdx2d_pixmap* pixmap, int32_t x, int32_t y, uint32_t col) { gdx2d_set_pixel(pixmap, x, y, col); }
        static uint32_t g2d_get_pixel(const gdx2d_pixmap* pixmap, int32_t x, int32_t y) { return gdx2d_get_pixel(pixmap, x, y); }
        static void g2d_draw_line(const gdx2d_pixmap* pixmap, int32_t x, int32_t y, int32_t x2, int32_t y2, uint32_t col) { gdx2d_draw_line(pixmap, x, y, x2, y2, col); }
        static void g2d_draw_rect(const gdx2d_pixmap* pixmap, int32_t x, int32_t y, uint32_t width, uint32_t height, uint32_t col) { gdx2d_draw_rect(pixmap, x, y, width, height, col); }
        static void g2d_draw_circle(const gdx2d_pixmap* pixmap, int32_t x, int32_t y, uint32_t radius, uint32_t col) { gdx2d_draw_circle(pixmap, x, y, radius, col); }
        static void g2d_fill_rect(const gdx2d_pixmap* pixmap, int32_t x, int32_t y, uint32_t width, uint32_t height, uint32_t col) { gdx2d_fill_rect(pixmap, x, y, width, height, col); }
        static void g2d_fill_circle(const gdx2d_pixmap* pixmap, int32_t x, int32_t y, uint32_t radius, uint32_t col) { gdx2d_fill_circle(pixmap, x, y, radius, col); }
        static void g2d_fill_triangle(const gdx2d_pixmap* pixmap, int32_t x1, int32_t y1, int32_t x2, int32_t y2, int32_t x3, int32_t y3, uint32_t col) { gdx2d_fill_triangle(pixmap, x1, y1, x2, y2, x3, y3, col); }
        static void g2d_draw_pixmap(const gdx2d_pixmap* src_pixmap,
                                       const gdx2d_pixmap* dst_pixmap,
                                       int32_t src_x, int32_t src_y, uint32_t src_width, uint32_t src_height,
                                       int32_t dst_x, int32_t dst_y, uint32_t dst_width, uint32_t dst_height) {
            gdx2d_draw_pixmap(src_pixmap, dst_pixmap, src_x, src_y, src_width, src_height, dst_x, dst_y, dst_width, dst_height);
        }
        static uint32_t g2d_bytes_per_pixel(uint32_t format) { return gdx2d_bytes_per_pixel(format); }
};