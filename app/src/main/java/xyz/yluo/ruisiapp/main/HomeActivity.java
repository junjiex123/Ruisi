package xyz.yluo.ruisiapp.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import xyz.yluo.ruisiapp.ConfigClass;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.TestActivity;
import xyz.yluo.ruisiapp.login.LoginActivity;
import xyz.yluo.ruisiapp.login.UserDakaActivity;
import xyz.yluo.ruisiapp.setting.AboutActivity;
import xyz.yluo.ruisiapp.setting.SettingActivity;

/**
 * Created by free2 on 16-3-17.
 *
 */
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.btn_1)
    protected Button btn1;
    @Bind(R.id.btn_2)
    protected Button btn2;
    @Bind(R.id.btn_3)
    protected Button btn3;
    @Bind(R.id.drawer_layout)
    protected DrawerLayout drawer;
    @Bind(R.id.nav_view)
    protected NavigationView navigationView;
    @Bind(R.id.main_radiogroup)
    protected RadioGroup main_radiogroup;
    @Bind(R.id.radio01)
    protected RadioButton radio01;
    @Bind(R.id.radio02)
    protected RadioButton radio02;

    private CheckBox show_zhidin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        View nav_header_login = header.findViewById(R.id.nav_header_login);
        View nav_header_notlogin = header.findViewById(R.id.nav_header_notlogin);
        show_zhidin = (CheckBox) header.findViewById(R.id.show_zhidin);
        show_zhidin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    show_zhidin.setText("不显示置顶");
                    ConfigClass.CONFIG_ISSHOW_ZHIDIN = true;

                }else{
                    show_zhidin.setText("显示置顶帖");
                    ConfigClass.CONFIG_ISSHOW_ZHIDIN = false;
                }
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        //判断是否登陆
        if (ConfigClass.CONFIG_ISLOGIN) {
            nav_header_login.setVisibility(View.VISIBLE);
            nav_header_notlogin.setVisibility(View.GONE);
        } else {
            nav_header_notlogin.setVisibility(View.VISIBLE);
            nav_header_login.setVisibility(View.GONE);
        }

        CircleImageView userImge = (CircleImageView) header.findViewById(R.id.profile_image);
        userImge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConfigClass.CONFIG_ISLOGIN) {
                    startActivity(new Intent(getApplicationContext(), UserDakaActivity.class));

                } else {
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivityForResult(i, 1);
                }
            }
        });


    }

    //登陆页面返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String result = data.getExtras().getString("result");//得到新Activity 关闭后返回的数据
            Toast.makeText(getApplicationContext(), "result" + result, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_1)
    protected void btn_1_click(){
        Fragment fragment1 = new HomeFragement_1();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, fragment1);
        transaction.commit();
    }
    @OnClick(R.id.btn_2)
    protected void btn_2_click(){
        Fragment fragment2 = new HomeFragement_2();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, fragment2);
        transaction.commit();

    }

    @OnClick(R.id.btn_3)
    protected void btn_3_click(){
        Fragment fragment3 = new HomeFragement_3();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, fragment3);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_test){
            startActivity(new Intent(this, TestActivity.class));
            // Handle the camera action
        }else if(id==R.id.nav_about){
            startActivity(new Intent(this,AboutActivity.class));
        }else if(id==R.id.nav_setting){
            startActivity(new Intent(getApplicationContext(), SettingActivity.class));
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
