package xyz.yluo.ruisiapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.fragment.ColorPickerDialog;
import xyz.yluo.ruisiapp.utils.PostHander;

/**
 * Created by free2 on 16-3-6.
 *
 */
public class NewArticleActivity extends AppCompatActivity {

    @Bind(R.id.edit_bar)
    protected LinearLayout edit_bar;

    @Bind(R.id.emotion_container)
    protected LinearLayout emotion_container;
    @Bind(R.id.edit_input_title)
    protected EditText edit_input_title;
    @Bind(R.id.edit_input_content)
    protected EditText edit_input_content;

    @Bind(R.id.action_bold)
    protected CheckBox action_bold;
    @Bind(R.id.action_italic)
    protected CheckBox action_italic;
    @Bind(R.id.action_color_text)
    protected CheckBox action_color_text;
    @Bind(R.id.action_list_c)
    protected CheckBox action_list_c;
    @Bind(R.id.action_list_n)
    protected CheckBox action_list_n;
    @Bind(R.id.action_emotion)
    protected CheckBox action_emotion;
    @Bind(R.id.action_image)
    protected CheckBox action_image;
    @Bind(R.id.action_link)
    protected CheckBox action_link;

    private int index =0;
    private String color = "#ff0000";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);
        ButterKnife.bind(this);



        action_bold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    edit_input_content.append("<b>");

                } else {
                    edit_input_content.append("</b>");

                }
            }
        });

        action_italic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    edit_input_content.append("<i>");

                } else {
                    edit_input_content.append("</i>");

                }
            }
        });

        action_list_c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ColorPickerDialog dialog = new ColorPickerDialog(
                        getApplicationContext(),
                        new ColorPickerDialog.OnColorChangedListener() {
                            @Override
                            public void colorChanged(String key, int color) {
                                System.out.print("\n>>>"+color);
                            }
                        },"111",0,0);

                dialog.show();
            }
        });

        action_color_text.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    edit_input_content.append("<font color=\""+color+"\">");

                } else {
                    edit_input_content.append("</font>");

                }
            }
        });


        //TODO
        //now preview
        action_image.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Editable message = edit_input_content.getEditableText();
                message.replace(0,edit_input_content.getSelectionEnd(), Html.fromHtml(edit_input_content.getText().toString()));
            }
        });

    }
    //
    //message.replace(index, ,edit_input_content.getText().subSequence(index,edit_input_content.getSelectionEnd()));

    @OnClick(R.id.action_emotion)
    protected void action_emotion(){
        //todo 可以加入动画
        if(emotion_container.getVisibility()==View.VISIBLE){
            emotion_container.setVisibility(View.GONE);
        }else{
            emotion_container.setVisibility(View.VISIBLE);
        }


    }

    @OnClick({R.id._1000, R.id._1001,R.id._1002,R.id._1003,R.id._1005,
            R.id._1006,R.id._1007,R.id._1008,R.id._1009,R.id._1010,
            R.id._1011,R.id._1012,R.id._1013,R.id._1014,R.id._1015,
            R.id._1016,R.id._1017,R.id._1018,R.id._1019,R.id._1020,
            R.id._1021,R.id._1022,R.id._1023,R.id._1024,R.id._1025,
            R.id._1027,R.id._1028,R.id._1029,R.id._1030, R.id._998,
            R.id._999,R.id._9998,R.id._9999
    })
    protected void smiley_click(ImageButton btn){
        //插入表情
        //{:16_1021:}
        //_1021
        //input_aera.append(btn.getTag().toString());
        String tmp = btn.getTag().toString();

        PostHander hander = new PostHander(getApplicationContext(),(EditText)getCurrentFocus());
        hander.insertSmiley("{:16" + tmp + ":}", btn.getDrawable());
    }
}
