package music.lisong.com.lisongmusic.fresco;

import android.net.Uri;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;


public class FrescoUtils {


    //type = 2
//	private static GenericDraweeHierarchy  getRoundCornorParam(Context context){
//		GenericDraweeHierarchy gdh = new GenericDraweeHierarchyBuilder(context.getResources())
//				.setPlaceholderImage(Drawables.sPlaceholderDrawable)
//				.setFailureImage(Drawables.sErrorDrawable)
//				.setRoundingParams(RoundingParams.fromCornersRadius(CommonUtils.dp2px(context, 3)))
//				.setProgressBarImage(null)
//				.build();
//		return gdh;
//	}
//	//type = 3
//	private static GenericDraweeHierarchy getNormalParam(Context context){
//		GenericDraweeHierarchy gdh = new GenericDraweeHierarchyBuilder(context.getResources())
//				.setPlaceholderImage(Drawables.sPlaceholderDrawable)
//				.setFailureImage(Drawables.sErrorDrawable)
//				.setProgressBarImage(null)
//				.build();
//		return gdh;
//	}
//	//type = 1
//	private static GenericDraweeHierarchy  getRoundingParam(Context context){
//		GenericDraweeHierarchy gdh = new GenericDraweeHierarchyBuilder(context.getResources())
//				.setPlaceholderImage(Drawables.sPlaceholderDrawable)
//				.setFailureImage(Drawables.sErrorDrawable)
//				.setRoundingParams(RoundingParams.asCircle().setBorder(Color.parseColor("#99E0C5F1"),
//						CommonUtils.dp2px(context, 3)))
//				.setProgressBarImage(null)
//				.build();
//		return gdh;
//	}

//    类型	Scheme	示例
//    远程图片	http://, https://	HttpURLConnection 或者参考 使用其他网络加载方案
//    本地文件	file://	FileInputStream
//    Content provider	content://	ContentResolver
//    asset目录下的资源	asset://	AssetManager
//    res目录下的资源	res://	Resources.openRawResource
//    res 示例:
//
//    Uri uri = Uri.parse("res://包名(实际可以是任何字符串甚至留空)/" + R.drawable.kaishuicon);

    public static void bindFrescoFromFile(
            SimpleDraweeView draweeView,
            String filePath
    ) {
        String uri = "file://" + filePath;

        ImageRequest imageRequest =
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                        .setProgressiveRenderingEnabled(true)
                        .build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(draweeView.getController())
                .setControllerListener(null)
                .setAutoPlayAnimations(true)
                .setTapToRetryEnabled(true)
                .build();
        draweeView.setController(draweeController);

    }

//
//    public static void bindFrescoFromResource(
//            SimpleDraweeView draweeView,
//            int id
//    ) {
//        String uri = "res://" + PlayingControlHelper.PACKAGENAME + "/" + id;
//
//        ImageRequest imageRequest =
//                ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
//                        .setProgressiveRenderingEnabled(true)
//                        .build();
//        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
//                .setOldController(draweeView.getController())
//                .setControllerListener(null)
//                .setAutoPlayAnimations(true)
//                .setTapToRetryEnabled(true)
//                .setImageRequest(imageRequest)
//                .build();
//        draweeView.setController(draweeController);
//
//
//    }

    public static void bindGifFromAsset(
            SimpleDraweeView draweeView, String assetGifName) {
        String uri = "asset:///"+assetGifName+".gif";
        DraweeController gifController = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
        draweeView.setController(gifController);
    }

    public static void bindGifFromResourceNew(SimpleDraweeView draweeView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Uri uri8Step = Uri.parse(url);
        DraweeController draweeController =
                Fresco.newDraweeControllerBuilder()
                        .setUri(uri8Step)
                        .setAutoPlayAnimations(true) // 设置加载图片完成后是否直接进行播放
                        .build();
        draweeView.setController(draweeController);
    }


    public static void bindgifFromResource(
            SimpleDraweeView draweeView,
            String ss) {
        DraweeController gifController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                .setUri(ss)
                .build();
        draweeView.setController(gifController);
    }

//    public static void bindFrescoFromResource(
//                                              SimpleDraweeView draweeView,
//                                              int id
//    ) {
//        String uri = "res://"+ PlayingControlHelper.PACKAGENAME+"/" + id;
//
//        draweeView.setImageURI(Uri.parse(uri));
//    }


    public static void bindFresco(
            DraweeView draweeView,
            String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        ImageRequest imageRequest =
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                        .build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(draweeView.getController())
                .setControllerListener(null)
                .setAutoPlayAnimations(true)
                .setTapToRetryEnabled(true)
                .build();
        draweeView.setController(draweeController);
    }

    public static void bindFresco(
            SimpleDraweeView draweeView,
            String url, ControllerListener ccc) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        ImageRequest imageRequest =
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                        .build();
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setOldController(draweeView.getController())
                .setControllerListener(ccc)
                .setAutoPlayAnimations(true)
                .setTapToRetryEnabled(true)
                .build();
        draweeView.setController(draweeController);
    }
}
