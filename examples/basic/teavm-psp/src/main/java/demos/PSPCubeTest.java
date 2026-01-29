package demos;

import com.github.xpenatan.gdx.teavm.backends.psp.utils.PSPGraphicsApi;

public class PSPCubeTest implements PSPTest {

    @Override
    public void create() {
    }

    @Override
    public void render() {
        PSPGraphicsApi.sceGuClearColor(0xFFFFFFFF);
        PSPGraphicsApi.sceGuClear(PSPGraphicsApi.GU_COLOR_BUFFER_BIT | PSPGraphicsApi.GU_DEPTH_BUFFER_BIT);
    }
}