package xyz.yluo.ruisiapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.App;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.View.CircleImageView;
import xyz.yluo.ruisiapp.httpUtil.HttpUtil;
import xyz.yluo.ruisiapp.httpUtil.ResponseHandler;
import xyz.yluo.ruisiapp.utils.GetId;
import xyz.yluo.ruisiapp.utils.UrlUtils;

/**
 * Created by free2 on 16-3-19.
 * 启动activity
 * 检查是否登陆
 * 读取相关设置写到{@link App}
 */
public class LaunchActivity extends BaseActivity implements View.OnClickListener{
    //等待时间
    private final static int WAIT_TIME = 200;
    private TextView launch_text;
    private CircleImageView user_image;
    private SharedPreferences shp = null;
    private boolean isForeGround = true;
    private Handler mHandler = new Handler();

    //记录2个检查网络的返回值，如果都为空说明没网...
    private String mobileRes = "";
    private String pcResponse = "";
    private boolean isLoginOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        String s = "<html><body><table id=\"fastpostsmiliesdiv_table\" cellpadding=\"0\" cellspacing=\"0\"><tbody><tr><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_366:}')\" id=\"fastpostsmilie_366_td\"><img id=\"smilie_366\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/035.gif\" alt=\"{:10_366:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_367:}')\" id=\"fastpostsmilie_367_td\" initialized=\"true\"><img id=\"smilie_367\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/109.gif\" alt=\"{:10_367:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_368:}')\" id=\"fastpostsmilie_368_td\"><img id=\"smilie_368\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/075.gif\" alt=\"{:10_368:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_369:}')\" id=\"fastpostsmilie_369_td\" initialized=\"true\"><img id=\"smilie_369\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/084.gif\" alt=\"{:10_369:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_370:}')\" id=\"fastpostsmilie_370_td\" initialized=\"true\"><img id=\"smilie_370\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/054.gif\" alt=\"{:10_370:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_371:}')\" id=\"fastpostsmilie_371_td\" initialized=\"true\"><img id=\"smilie_371\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/077.gif\" alt=\"{:10_371:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_372:}')\" id=\"fastpostsmilie_372_td\" initialized=\"true\"><img id=\"smilie_372\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/092.gif\" alt=\"{:10_372:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_373:}')\" id=\"fastpostsmilie_373_td\" initialized=\"true\"><img id=\"smilie_373\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/067.gif\" alt=\"{:10_373:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_374:}')\" id=\"fastpostsmilie_374_td\"><img id=\"smilie_374\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/026.gif\" alt=\"{:10_374:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_375:}')\" id=\"fastpostsmilie_375_td\"><img id=\"smilie_375\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/081.gif\" alt=\"{:10_375:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_376:}')\" id=\"fastpostsmilie_376_td\"><img id=\"smilie_376\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/031.gif\" alt=\"{:10_376:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_377:}')\" id=\"fastpostsmilie_377_td\"><img id=\"smilie_377\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/083.gif\" alt=\"{:10_377:}\"></td></tr><tr><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_378:}')\" id=\"fastpostsmilie_378_td\"><img id=\"smilie_378\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/041.gif\" alt=\"{:10_378:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_379:}')\" id=\"fastpostsmilie_379_td\" initialized=\"true\"><img id=\"smilie_379\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/102.gif\" alt=\"{:10_379:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_380:}')\" id=\"fastpostsmilie_380_td\"><img id=\"smilie_380\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/072.gif\" alt=\"{:10_380:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_381:}')\" id=\"fastpostsmilie_381_td\"><img id=\"smilie_381\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/048.gif\" alt=\"{:10_381:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_382:}')\" id=\"fastpostsmilie_382_td\" initialized=\"true\"><img id=\"smilie_382\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/052.gif\" alt=\"{:10_382:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_383:}')\" id=\"fastpostsmilie_383_td\" initialized=\"true\"><img id=\"smilie_383\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/079.gif\" alt=\"{:10_383:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_384:}')\" id=\"fastpostsmilie_384_td\" initialized=\"true\"><img id=\"smilie_384\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/014.gif\" alt=\"{:10_384:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_385:}')\" id=\"fastpostsmilie_385_td\"><img id=\"smilie_385\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/087.gif\" alt=\"{:10_385:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_386:}')\" id=\"fastpostsmilie_386_td\"><img id=\"smilie_386\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/076.gif\" alt=\"{:10_386:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_387:}')\" id=\"fastpostsmilie_387_td\"><img id=\"smilie_387\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/057.gif\" alt=\"{:10_387:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_388:}')\" id=\"fastpostsmilie_388_td\"><img id=\"smilie_388\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/108.gif\" alt=\"{:10_388:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_389:}')\" id=\"fastpostsmilie_389_td\"><img id=\"smilie_389\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/078.gif\" alt=\"{:10_389:}\"></td></tr><tr><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_390:}')\" id=\"fastpostsmilie_390_td\"><img id=\"smilie_390\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/082.gif\" alt=\"{:10_390:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_391:}')\" id=\"fastpostsmilie_391_td\" initialized=\"true\"><img id=\"smilie_391\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/017.gif\" alt=\"{:10_391:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_392:}')\" id=\"fastpostsmilie_392_td\" initialized=\"true\"><img id=\"smilie_392\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/019.gif\" alt=\"{:10_392:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_393:}')\" id=\"fastpostsmilie_393_td\"><img id=\"smilie_393\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/029.gif\" alt=\"{:10_393:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_394:}')\" id=\"fastpostsmilie_394_td\"><img id=\"smilie_394\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/024.gif\" alt=\"{:10_394:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_395:}')\" id=\"fastpostsmilie_395_td\"><img id=\"smilie_395\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/085.gif\" alt=\"{:10_395:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_396:}')\" id=\"fastpostsmilie_396_td\"><img id=\"smilie_396\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/063.gif\" alt=\"{:10_396:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_397:}')\" id=\"fastpostsmilie_397_td\"><img id=\"smilie_397\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/055.gif\" alt=\"{:10_397:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_398:}')\" id=\"fastpostsmilie_398_td\"><img id=\"smilie_398\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/001.gif\" alt=\"{:10_398:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_399:}')\" id=\"fastpostsmilie_399_td\"><img id=\"smilie_399\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/089.gif\" alt=\"{:10_399:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_400:}')\" id=\"fastpostsmilie_400_td\"><img id=\"smilie_400\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/062.gif\" alt=\"{:10_400:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_401:}')\" id=\"fastpostsmilie_401_td\"><img id=\"smilie_401\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/027.gif\" alt=\"{:10_401:}\"></td></tr><tr><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_402:}')\" id=\"fastpostsmilie_402_td\"><img id=\"smilie_402\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/086.gif\" alt=\"{:10_402:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_403:}')\" id=\"fastpostsmilie_403_td\"><img id=\"smilie_403\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/110.gif\" alt=\"{:10_403:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_404:}')\" id=\"fastpostsmilie_404_td\" initialized=\"true\"><img id=\"smilie_404\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/091.gif\" alt=\"{:10_404:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_405:}')\" id=\"fastpostsmilie_405_td\"><img id=\"smilie_405\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/007.gif\" alt=\"{:10_405:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_406:}')\" id=\"fastpostsmilie_406_td\"><img id=\"smilie_406\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/042.gif\" alt=\"{:10_406:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_407:}')\" id=\"fastpostsmilie_407_td\"><img id=\"smilie_407\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/093.gif\" alt=\"{:10_407:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_408:}')\" id=\"fastpostsmilie_408_td\"><img id=\"smilie_408\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/016.gif\" alt=\"{:10_408:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_409:}')\" id=\"fastpostsmilie_409_td\" initialized=\"true\"><img id=\"smilie_409\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/040.gif\" alt=\"{:10_409:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_410:}')\" id=\"fastpostsmilie_410_td\" initialized=\"true\"><img id=\"smilie_410\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/020.gif\" alt=\"{:10_410:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_411:}')\" id=\"fastpostsmilie_411_td\"><img id=\"smilie_411\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/103.gif\" alt=\"{:10_411:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_412:}')\" id=\"fastpostsmilie_412_td\"><img id=\"smilie_412\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/045.gif\" alt=\"{:10_412:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_413:}')\" id=\"fastpostsmilie_413_td\"><img id=\"smilie_413\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/006.gif\" alt=\"{:10_413:}\"></td></tr><tr><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_414:}')\" id=\"fastpostsmilie_414_td\"><img id=\"smilie_414\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/015.gif\" alt=\"{:10_414:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_415:}')\" id=\"fastpostsmilie_415_td\" initialized=\"true\"><img id=\"smilie_415\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/039.gif\" alt=\"{:10_415:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_416:}')\" id=\"fastpostsmilie_416_td\" initialized=\"true\"><img id=\"smilie_416\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/058.gif\" alt=\"{:10_416:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_417:}')\" id=\"fastpostsmilie_417_td\" initialized=\"true\"><img id=\"smilie_417\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/088.gif\" alt=\"{:10_417:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_418:}')\" id=\"fastpostsmilie_418_td\"><img id=\"smilie_418\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/011.gif\" alt=\"{:10_418:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_419:}')\" id=\"fastpostsmilie_419_td\"><img id=\"smilie_419\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/032.gif\" alt=\"{:10_419:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_420:}')\" id=\"fastpostsmilie_420_td\"><img id=\"smilie_420\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/043.gif\" alt=\"{:10_420:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_421:}')\" id=\"fastpostsmilie_421_td\"><img id=\"smilie_421\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/066.gif\" alt=\"{:10_421:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_422:}')\" id=\"fastpostsmilie_422_td\"><img id=\"smilie_422\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/061.gif\" alt=\"{:10_422:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_423:}')\" id=\"fastpostsmilie_423_td\"><img id=\"smilie_423\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/028.gif\" alt=\"{:10_423:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_424:}')\" id=\"fastpostsmilie_424_td\"><img id=\"smilie_424\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/005.gif\" alt=\"{:10_424:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_425:}')\" id=\"fastpostsmilie_425_td\"><img id=\"smilie_425\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/069.gif\" alt=\"{:10_425:}\"></td></tr>\n" +
                "<tr><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_426:}')\" id=\"fastpostsmilie_426_td\"><img id=\"smilie_426\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/107.gif\" alt=\"{:10_426:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_427:}')\" id=\"fastpostsmilie_427_td\"><img id=\"smilie_427\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/059.gif\" alt=\"{:10_427:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_428:}')\" id=\"fastpostsmilie_428_td\"><img id=\"smilie_428\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/009.gif\" alt=\"{:10_428:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_429:}')\" id=\"fastpostsmilie_429_td\"><img id=\"smilie_429\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/036.gif\" alt=\"{:10_429:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_430:}')\" id=\"fastpostsmilie_430_td\"><img id=\"smilie_430\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/038.gif\" alt=\"{:10_430:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_431:}')\" id=\"fastpostsmilie_431_td\"><img id=\"smilie_431\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/025.gif\" alt=\"{:10_431:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_432:}')\" id=\"fastpostsmilie_432_td\"><img id=\"smilie_432\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/021.gif\" alt=\"{:10_432:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_433:}')\" id=\"fastpostsmilie_433_td\"><img id=\"smilie_433\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/112.gif\" alt=\"{:10_433:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_434:}')\" id=\"fastpostsmilie_434_td\"><img id=\"smilie_434\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/073.gif\" alt=\"{:10_434:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_435:}')\" id=\"fastpostsmilie_435_td\"><img id=\"smilie_435\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/022.gif\" alt=\"{:10_435:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_436:}')\" id=\"fastpostsmilie_436_td\"><img id=\"smilie_436\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/010.gif\" alt=\"{:10_436:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_437:}')\" id=\"fastpostsmilie_437_td\"><img id=\"smilie_437\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/090.gif\" alt=\"{:10_437:}\"></td></tr><tr><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_438:}')\" id=\"fastpostsmilie_438_td\"><img id=\"smilie_438\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/105.gif\" alt=\"{:10_438:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_439:}')\" id=\"fastpostsmilie_439_td\"><img id=\"smilie_439\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/106.gif\" alt=\"{:10_439:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_440:}')\" id=\"fastpostsmilie_440_td\"><img id=\"smilie_440\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/094.gif\" alt=\"{:10_440:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_441:}')\" id=\"fastpostsmilie_441_td\"><img id=\"smilie_441\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/050.gif\" alt=\"{:10_441:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_442:}')\" id=\"fastpostsmilie_442_td\"><img id=\"smilie_442\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/095.gif\" alt=\"{:10_442:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_443:}')\" id=\"fastpostsmilie_443_td\"><img id=\"smilie_443\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/037.gif\" alt=\"{:10_443:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_444:}')\" id=\"fastpostsmilie_444_td\"><img id=\"smilie_444\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/044.gif\" alt=\"{:10_444:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_445:}')\" id=\"fastpostsmilie_445_td\"><img id=\"smilie_445\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/012.gif\" alt=\"{:10_445:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_446:}')\" id=\"fastpostsmilie_446_td\"><img id=\"smilie_446\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/013.gif\" alt=\"{:10_446:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_447:}')\" id=\"fastpostsmilie_447_td\"><img id=\"smilie_447\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/100.gif\" alt=\"{:10_447:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_448:}')\" id=\"fastpostsmilie_448_td\"><img id=\"smilie_448\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/099.gif\" alt=\"{:10_448:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_449:}')\" id=\"fastpostsmilie_449_td\"><img id=\"smilie_449\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/101.gif\" alt=\"{:10_449:}\"></td></tr><tr><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_450:}')\" id=\"fastpostsmilie_450_td\"><img id=\"smilie_450\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/023.gif\" alt=\"{:10_450:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_451:}')\" id=\"fastpostsmilie_451_td\"><img id=\"smilie_451\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/064.gif\" alt=\"{:10_451:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_452:}')\" id=\"fastpostsmilie_452_td\"><img id=\"smilie_452\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/096.gif\" alt=\"{:10_452:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_453:}')\" id=\"fastpostsmilie_453_td\"><img id=\"smilie_453\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/065.gif\" alt=\"{:10_453:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_454:}')\" id=\"fastpostsmilie_454_td\"><img id=\"smilie_454\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/053.gif\" alt=\"{:10_454:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_455:}')\" id=\"fastpostsmilie_455_td\"><img id=\"smilie_455\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/060.gif\" alt=\"{:10_455:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_456:}')\" id=\"fastpostsmilie_456_td\"><img id=\"smilie_456\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/033.gif\" alt=\"{:10_456:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_457:}')\" id=\"fastpostsmilie_457_td\"><img id=\"smilie_457\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/074.gif\" alt=\"{:10_457:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_458:}')\" id=\"fastpostsmilie_458_td\"><img id=\"smilie_458\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/047.gif\" alt=\"{:10_458:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_459:}')\" id=\"fastpostsmilie_459_td\"><img id=\"smilie_459\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/018.gif\" alt=\"{:10_459:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_460:}')\" id=\"fastpostsmilie_460_td\"><img id=\"smilie_460\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/004.gif\" alt=\"{:10_460:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_461:}')\" id=\"fastpostsmilie_461_td\"><img id=\"smilie_461\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/080.gif\" alt=\"{:10_461:}\"></td></tr><tr><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_462:}')\" id=\"fastpostsmilie_462_td\"><img id=\"smilie_462\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/097.gif\" alt=\"{:10_462:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_463:}')\" id=\"fastpostsmilie_463_td\"><img id=\"smilie_463\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/003.gif\" alt=\"{:10_463:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_464:}')\" id=\"fastpostsmilie_464_td\"><img id=\"smilie_464\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/068.gif\" alt=\"{:10_464:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_465:}')\" id=\"fastpostsmilie_465_td\"><img id=\"smilie_465\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/002.gif\" alt=\"{:10_465:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_466:}')\" id=\"fastpostsmilie_466_td\"><img id=\"smilie_466\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/070.gif\" alt=\"{:10_466:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_467:}')\" id=\"fastpostsmilie_467_td\"><img id=\"smilie_467\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/051.gif\" alt=\"{:10_467:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_468:}')\" id=\"fastpostsmilie_468_td\"><img id=\"smilie_468\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/056.gif\" alt=\"{:10_468:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_469:}')\" id=\"fastpostsmilie_469_td\"><img id=\"smilie_469\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/034.gif\" alt=\"{:10_469:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_470:}')\" id=\"fastpostsmilie_470_td\"><img id=\"smilie_470\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/071.gif\" alt=\"{:10_470:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_471:}')\" id=\"fastpostsmilie_471_td\"><img id=\"smilie_471\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/008.gif\" alt=\"{:10_471:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_472:}')\" id=\"fastpostsmilie_472_td\"><img id=\"smilie_472\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/104.gif\" alt=\"{:10_472:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_473:}')\" id=\"fastpostsmilie_473_td\"><img id=\"smilie_473\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/049.gif\" alt=\"{:10_473:}\"></td></tr><tr><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_474:}')\" id=\"fastpostsmilie_474_td\"><img id=\"smilie_474\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/098.gif\" alt=\"{:10_474:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_475:}')\" id=\"fastpostsmilie_475_td\" initialized=\"true\"><img id=\"smilie_475\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/111.gif\" alt=\"{:10_475:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_476:}')\" id=\"fastpostsmilie_476_td\" initialized=\"true\"><img id=\"smilie_476\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/030.gif\" alt=\"{:10_476:}\"></td><td onmouseover=\"smilies_preview('fastpost', 'fastpostsmiliesdiv', this, 50)\" onclick=\"seditor_insertunit('fastpost', '{:10_477:}')\" id=\"fastpostsmilie_477_td\" initialized=\"true\"><img id=\"smilie_477\" width=\"40\" height=\"40\" src=\"static/image/smiley/ali/046.gif\" alt=\"{:10_477:}\"></td></tr></tbody></table></body></html>";

        String smiley_dir = "static/image/smiley/ali";
        List<String> smileys = new ArrayList<>();
        String[] ss;
        try {
            ss = getAssets().list(smiley_dir);
            for(String t:ss){
                smileys.add(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Document doc =  Jsoup.parse(s);
        Elements els = doc.select("tr");
        for(Element ee:els.select("img")){
            String src = ee.attr("src");
            String name = ee.attr("alt");

            String sss = src.substring(src.lastIndexOf("/")+1);
            if(smileys.contains(sss)){
                //case "acn015":
                //insertName = "15_933";
                //break;
                System.out.println("case \""+sss+ "\": insertName = \""+name.substring(2,name.length()-2)+"\"; break;");
            }

        }


        launch_text = (TextView) findViewById(R.id.launch_text);
        findViewById(R.id.btn_login_inner).setOnClickListener(this);
        findViewById(R.id.btn_login_outer).setOnClickListener(this);
        findViewById(R.id.login_fail_view).setVisibility(View.INVISIBLE);
        user_image = (CircleImageView) findViewById(R.id.user_image);
        user_image.setVisibility(View.GONE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        shp = getSharedPreferences(App.MY_SHP_NAME,MODE_PRIVATE);
        String uid = App.getUid(this);
        if (!TextUtils.isEmpty(uid)) {
            String url = UrlUtils.getAvaterurlm(uid);
            Picasso.with(this).load(url).placeholder(R.drawable.image_placeholder).into(user_image);
            user_image.setVisibility(View.VISIBLE);
        }
        mHandler.postDelayed(finishRunable, 3000);
        final String urlin = "http://rs.xidian.edu.cn/member.php?mod=logging&action=login&mobile=2";
        final String urlout = "http://bbs.rs.xidian.me/member.php?mod=logging&action=login&mobile=2";

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpUtil.get(LaunchActivity.this, urlin, new ResponseHandler() {
                    @Override
                    public void onSuccess(byte[] response) {
                        pcResponse = new String(response);
                        loginOk();
                        isLoginOk = true;
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        super.onFailure(e);
                        e.printStackTrace();
                        Log.e("login fial","====inner=====");
                    }
                });

                try {
                    Thread.sleep(350);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if(!isLoginOk){
                    HttpUtil.get(LaunchActivity.this, urlout, new ResponseHandler() {
                        @Override
                        public void onSuccess(byte[] response) {
                            mobileRes  = new String(response);
                            if(!isLoginOk){
                                isLoginOk = true;
                                loginOk();
                            }
                        }
                    });
                }
            }
        }).start();



    }


    private Runnable finishRunable = new Runnable() {
        @Override
        public void run() {
            if(!isLoginOk)
                loginOk();
        }
    };

    private void loginOk(){
        if(!isLoginOk){
            mHandler.removeCallbacks(finishRunable);
            if(isForeGround){
                new CheckTask().execute();
            }
        }

    }

    private void enterHome(){
        if(isForeGround){
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login_inner:
                App.IS_SCHOOL_NET = true;
                enterHome();
                break;
            case R.id.btn_login_outer:
                App.IS_SCHOOL_NET = false;
                enterHome();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.4f, 1.0f);
        alphaAnimation.setDuration((long) (WAIT_TIME*0.85));// 设置动画显示时间
        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.always_rotate);
        findViewById(R.id.loading_view).startAnimation(rotateAnimation);
        launch_text.startAnimation(alphaAnimation);
        user_image.startAnimation(alphaAnimation);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeGround = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(finishRunable);
        isForeGround = false;
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(finishRunable);
        super.onDestroy();
    }

    private class CheckTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            String res = "";
            if(!TextUtils.isEmpty(pcResponse)){
                App.IS_SCHOOL_NET = true;
                res = pcResponse;
            }else if(!TextUtils.isEmpty(mobileRes)){
                App.IS_SCHOOL_NET = false;
                res = mobileRes;
            }
            if(!TextUtils.isEmpty(res)){
                int i = res.indexOf("欢迎您回来");
                if(i>0){
                    String info = res.substring(i+6,i+26);
                    int pos1 = info.indexOf(" ");
                    int pos2 = info.indexOf("，");
                    String grade = info.substring(0,pos1);
                    String name = info.substring(pos1+1,pos2);
                    String uid = GetId.getid("uid=",res.substring(i));
                    int indexhash = res.indexOf("formhash");
                    String hash = res.substring(indexhash+9,indexhash+17);
                    SharedPreferences.Editor ed =  shp.edit();
                    ed.putString(App.USER_UID_KEY,uid);
                    ed.putString(App.USER_NAME_KEY,name);
                    ed.putString(App.USER_GRADE_KEY,grade);
                    ed.putString(App.HASH_KEY,hash);
                    ed.apply();
                    Log.e("res","grade "+grade+" uid "+uid+" name "+name+" hash "+hash);
                }
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean) {
                enterHome();
            }else{
                Toast.makeText(LaunchActivity.this, "没有网络,或者睿思服务器又崩溃了！",
                        Toast.LENGTH_SHORT).show();
                findViewById(R.id.login_view).setVisibility(View.GONE);
                View fail = findViewById(R.id.login_fail_view);
                fail.setVisibility(View.VISIBLE);
            }
        }
    }
}
