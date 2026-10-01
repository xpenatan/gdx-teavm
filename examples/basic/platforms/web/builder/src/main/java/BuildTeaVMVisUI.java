import com.badlogic.gdx.Files.FileType;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilder;
import com.github.xpenatan.gdx.teavm.backends.web.config.backend.WebBackend;
import java.io.File;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildTeaVMVisUI {

    public static void main(String[] args) {
        AssetFileHandle assetsPath = new AssetFileHandle("../../../assets");

        TeaBuilder builder = new TeaBuilder(new WebBackend().setStartJettyAfterBuild(true))
                .addAssets(assetsPath)
                .addAssets(new AssetFileHandle("com/kotcrab/vis/ui/skin/x1", FileType.Classpath))
                .addAssets(new AssetFileHandle("com/kotcrab/vis/ui/widget/color/internal", FileType.Classpath))
                .addAssets(new AssetFileHandle("com/kotcrab/vis/ui/i18n/ColorPicker", FileType.Classpath))
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(TestWebLauncher.class.getName())
                .setObfuscated(false);
        // Concrete types named by VisUI's bundled x1 skin; the application policy grants their members.
        for(String styleClass : new String[] {
                "com.kotcrab.vis.ui.Sizes",
                "com.kotcrab.vis.ui.widget.VisTextField$VisTextFieldStyle",
                "com.kotcrab.vis.ui.widget.VisTextButton$VisTextButtonStyle",
                "com.kotcrab.vis.ui.widget.VisImageButton$VisImageButtonStyle",
                "com.kotcrab.vis.ui.widget.VisImageTextButton$VisImageTextButtonStyle",
                "com.kotcrab.vis.ui.widget.VisCheckBox$VisCheckBoxStyle",
                "com.kotcrab.vis.ui.widget.PopupMenu$PopupMenuStyle",
                "com.kotcrab.vis.ui.widget.Menu$MenuStyle",
                "com.kotcrab.vis.ui.widget.MenuBar$MenuBarStyle",
                "com.kotcrab.vis.ui.widget.Separator$SeparatorStyle",
                "com.kotcrab.vis.ui.widget.VisSplitPane$VisSplitPaneStyle",
                "com.kotcrab.vis.ui.widget.MultiSplitPane$MultiSplitPaneStyle",
                "com.kotcrab.vis.ui.widget.MenuItem$MenuItemStyle",
                "com.kotcrab.vis.ui.widget.Tooltip$TooltipStyle",
                "com.kotcrab.vis.ui.widget.LinkLabel$LinkLabelStyle",
                "com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane$TabbedPaneStyle",
                "com.kotcrab.vis.ui.widget.spinner.Spinner$SpinnerStyle",
                "com.kotcrab.vis.ui.widget.file.FileChooserStyle",
                "com.kotcrab.vis.ui.widget.color.ColorPickerWidgetStyle",
                "com.kotcrab.vis.ui.widget.color.ColorPickerStyle",
                "com.kotcrab.vis.ui.widget.toast.Toast$ToastStyle",
                "com.kotcrab.vis.ui.widget.BusyBar$BusyBarStyle",
                "com.kotcrab.vis.ui.widget.ListViewStyle",
                "com.kotcrab.vis.ui.util.form.SimpleFormValidator$FormValidatorStyle",
                "com.kotcrab.vis.ui.util.adapter.SimpleListAdapter$SimpleListAdapterStyle"
        }) {
            builder.addPreservedClass(styleClass);
        }
        builder.build(new File("build/dist"));
    }
}
