package com.github.xpenatan.gdx.backends.teavm.example;

import java.io.File;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.GlyphLayout.GlyphRun;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage.TouchFocus;
import com.badlogic.gdx.scenes.scene2d.actions.AddAction;
import com.badlogic.gdx.scenes.scene2d.actions.AddListenerAction;
import com.badlogic.gdx.scenes.scene2d.actions.AfterAction;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.scenes.scene2d.actions.CountdownEventAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelegateAction;
import com.badlogic.gdx.scenes.scene2d.actions.EventAction;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import com.badlogic.gdx.scenes.scene2d.actions.IntAction;
import com.badlogic.gdx.scenes.scene2d.actions.LayoutAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RelativeTemporalAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveListenerAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.actions.SizeByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SizeToAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.actions.TimeScaleAction;
import com.badlogic.gdx.scenes.scene2d.actions.TouchableAction;
import com.badlogic.gdx.scenes.scene2d.actions.VisibleAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener.FocusEvent;
import com.badlogic.gdx.utils.Array;
import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.TeaReflectionSupplier;

public class Build {

	public static void main(String[] args) {

//		try {
//
//			System.out.println("Main");
//			System.setProperty("teavm.junit.js.runner", "htmlunit");
////			System.setProperty("teavm.junit.js.runner", "browser-chrome");
////			System.setProperty("teavm.junit.js.runner", "browser");
//			System.setProperty("teavm.junit.target", "C:\\Test");
//			System.setProperty("teavm.junit.js.decodeStack", "false");
//			TeaVMTestRunner teaVMTestRunner = new TeaVMTestRunner(SerializerTest2.class);
//			teaVMTestRunner.run(new RunNotifier() {
//
//			});
//		}
//		catch (InitializationError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}


		TeaReflectionSupplier.addReflectionClass(ButtonStyle.class);
		TeaReflectionSupplier.addReflectionClass(TextButtonStyle.class);
		TeaReflectionSupplier.addReflectionClass(ScrollPaneStyle.class);
		TeaReflectionSupplier.addReflectionClass(SelectBoxStyle.class);
		TeaReflectionSupplier.addReflectionClass(ListStyle.class);
		TeaReflectionSupplier.addReflectionClass(SplitPaneStyle.class);
		TeaReflectionSupplier.addReflectionClass(WindowStyle.class);
		TeaReflectionSupplier.addReflectionClass(ProgressBarStyle.class);
		TeaReflectionSupplier.addReflectionClass(LabelStyle.class);
		TeaReflectionSupplier.addReflectionClass(TextFieldStyle.class);
		TeaReflectionSupplier.addReflectionClass(CheckBoxStyle.class);
		TeaReflectionSupplier.addReflectionClass(TouchpadStyle.class);
		TeaReflectionSupplier.addReflectionClass(TreeStyle.class);
		TeaReflectionSupplier.addReflectionClass(TextTooltipStyle.class);
		TeaReflectionSupplier.addReflectionClass(InputEvent.class);
		TeaReflectionSupplier.addReflectionClass(GlyphRun.class);
		TeaReflectionSupplier.addReflectionClass(Color.class);
		TeaReflectionSupplier.addReflectionClass(Array.class);
		TeaReflectionSupplier.addReflectionClass(ChangeEvent.class);
		TeaReflectionSupplier.addReflectionClass(GlyphLayout.class);
		TeaReflectionSupplier.addReflectionClass(FocusEvent.class);
		TeaReflectionSupplier.addReflectionClass(Rectangle.class);
		TeaReflectionSupplier.addReflectionClass(TouchFocus.class);

		TeaReflectionSupplier.addReflectionClass(VisibleAction.class);
		TeaReflectionSupplier.addReflectionClass(TouchableAction.class);
		TeaReflectionSupplier.addReflectionClass(TimeScaleAction.class);
		TeaReflectionSupplier.addReflectionClass(TemporalAction.class);
		TeaReflectionSupplier.addReflectionClass(SizeToAction.class);
		TeaReflectionSupplier.addReflectionClass(SizeByAction.class);
		TeaReflectionSupplier.addReflectionClass(SequenceAction.class);
		TeaReflectionSupplier.addReflectionClass(ScaleToAction.class);
		TeaReflectionSupplier.addReflectionClass(ScaleByAction.class);
		TeaReflectionSupplier.addReflectionClass(RunnableAction.class);
		TeaReflectionSupplier.addReflectionClass(RotateToAction.class);
		TeaReflectionSupplier.addReflectionClass(RotateByAction.class);
		TeaReflectionSupplier.addReflectionClass(RepeatAction.class);
		TeaReflectionSupplier.addReflectionClass(RemoveListenerAction.class);
		TeaReflectionSupplier.addReflectionClass(RemoveActorAction.class);
		TeaReflectionSupplier.addReflectionClass(RemoveAction.class);
		TeaReflectionSupplier.addReflectionClass(RelativeTemporalAction.class);
		TeaReflectionSupplier.addReflectionClass(ParallelAction.class);
		TeaReflectionSupplier.addReflectionClass(MoveToAction.class);
		TeaReflectionSupplier.addReflectionClass(MoveByAction.class);
		TeaReflectionSupplier.addReflectionClass(LayoutAction.class);
		TeaReflectionSupplier.addReflectionClass(IntAction.class);
		TeaReflectionSupplier.addReflectionClass(FloatAction.class);
		TeaReflectionSupplier.addReflectionClass(EventAction.class);
		TeaReflectionSupplier.addReflectionClass(DelegateAction.class);
		TeaReflectionSupplier.addReflectionClass(DelayAction.class);
		TeaReflectionSupplier.addReflectionClass(CountdownEventAction.class);
		TeaReflectionSupplier.addReflectionClass(ColorAction.class);
		TeaReflectionSupplier.addReflectionClass(AlphaAction.class);
		TeaReflectionSupplier.addReflectionClass(AfterAction.class);
		TeaReflectionSupplier.addReflectionClass(AddListenerAction.class);
		TeaReflectionSupplier.addReflectionClass(AddAction.class);

		TeaBuilder.build(new TeaBuildConfiguration() {

			@Override
			public Class getMainClass() {
				return Launcher.class;
			}

			@Override
			public String getWebAppPath() {
				return new File("webapp").getAbsolutePath();
			}

			@Override
			public boolean minifying() {
				return false;
			}


			@Override
			public boolean assetsPath(Array<File> paths) {
				File assetPath = new File("../example-core-desktop/assets");
				paths.add(assetPath);
				return true;
			}

			@Override
			public void assetsClasspath(Array<String> classPaths) {

			}
		});
	}
}
