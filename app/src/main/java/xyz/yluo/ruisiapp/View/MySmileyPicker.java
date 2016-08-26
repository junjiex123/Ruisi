package xyz.yluo.ruisiapp.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.adapter.SmileyAdapter;
import xyz.yluo.ruisiapp.listener.ListItemClickListener;

/**
 * Created by free2 on 16-7-19.
 *
 */

public class MySmileyPicker extends PopupWindow{

    private Context mContext;
    private OnItemClickListener listener;
    private RecyclerView recyclerView;
    private SmileyAdapter adapter;
    private List<Drawable> ds = new ArrayList<>();
    private String[] nameList;

    private static final int SMILEY_TB = 1;
    private static final int SMILEY_LDB = 2;
    private static final int SMILEY_ACN = 3;

    private int smiley_type = SMILEY_TB;


    public MySmileyPicker(Context context)
    {
        super(context);
        mContext = context;
        init();
    }


    private void init()
    {

        View v = LayoutInflater.from(mContext).inflate(R.layout.my_smiley_view,null);
        TabLayout tab = (TabLayout) v.findViewById(R.id.mytab);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        ds = getSmileys();
        tab.addTab(tab.newTab().setText("贴吧"));
        tab.addTab(tab.newTab().setText("林大b"));
        tab.addTab(tab.newTab().setText("AC娘"));
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 7, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SmileyAdapter(new ListItemClickListener() {
            @Override
            public void onListItemClick(View v, int position) {
                smileyClick(position);
                dismiss();
            }
        }, ds);

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        smiley_type = SMILEY_TB;
                        break;
                    case 1:
                        smiley_type = SMILEY_LDB;
                        break;
                    case 2:
                        smiley_type = SMILEY_ACN;
                        break;
                }
                changeSmiley();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        recyclerView.setAdapter(adapter);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(ContextCompat.getDrawable(mContext,R.drawable.rec_solid_primary_bg));
        setFocusable(true);
        setContentView(v);
    }


    private void changeSmiley() {
        ds.clear();
        ds = getSmileys();
        adapter.notifyDataSetChanged();
    }


    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void itemClick(String str,Drawable a);
    }

    private List<Drawable> getSmileys() {
        String smiley_dir = "static/image/smiley/";
        if (smiley_type == SMILEY_TB) {
            smiley_dir += "tieba";
        } else if (smiley_type == SMILEY_LDB) {
            smiley_dir += "lindab";
        } else if (smiley_type == SMILEY_ACN) {
            smiley_dir += "acn";
        }

        try {
            nameList = mContext.getAssets().list(smiley_dir);
            for (String temp : nameList) {
                InputStream in = mContext.getAssets().open(smiley_dir + "/" + temp);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);
                d.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                ds.add(d);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ds;
    }

    private void smileyClick(int position) {
        if (position > nameList.length) {
            return;
        }
        String name = nameList[position].split("\\.")[0];
        String insertName = "";
        if (smiley_type == SMILEY_TB) {
            switch (name) {
                case "tb001":
                    insertName = "16_1014";
                    break;
                case "tb002":
                    insertName = "16_1006";
                    break;
                case "tb003":
                    insertName = "16_1018";
                    break;
                case "tb004":
                    insertName = "16_1001";
                    break;
                case "tb005":
                    insertName = "16_1019";
                    break;
                case "tb006":
                    insertName = "16_1025";
                    break;
                case "tb008":
                    insertName = "16_1013";
                    break;
                case "tb009":
                    insertName = "16_1024";
                    break;
                case "tb010":
                    insertName = "16_1020";
                    break;
                case "tb011":
                    insertName = "16_1022";
                    break;
                case "tb012":
                    insertName = "16_1000";
                    break;
                case "tb013":
                    insertName = "16_999";
                    break;
                case "tb014":
                    insertName = "16_998";
                    break;
                case "tb015":
                    insertName = "16_1028";
                    break;
                case "tb016":
                    insertName = "16_1017";
                    break;
                case "tb017":
                    insertName = "16_1023";
                    break;
                case "tb018":
                    insertName = "16_1009";
                    break;
                case "tb019":
                    insertName = "16_1030";
                    break;
                case "tb020":
                    insertName = "16_1007";
                    break;
                case "tb021":
                    insertName = "16_1015";
                    break;
                case "tb022":
                    insertName = "16_1011";
                    break;
                case "tb023":
                    insertName = "16_1029";
                    break;
                case "tb024":
                    insertName = "16_1003";
                    break;
                case "tb025":
                    insertName = "16_1016";
                    break;
                case "tb026":
                    insertName = "16_1008";
                    break;
                case "tb027":
                    insertName = "16_1002";
                    break;
                case "tb028":
                    insertName = "16_1005";
                    break;
                case "tb029":
                    insertName = "16_1027";
                    break;
                case "tb030":
                    insertName = "16_1010";
                    break;
                case "tb031":
                    insertName = "16_1012";
                    break;
                case "tb032":
                    insertName = "16_1021";
                    break;
            }
        } else if (smiley_type == SMILEY_LDB) {
            switch (name) {
                case "ldb033":
                    insertName = "14_860";
                    break;
                case "ldb074":
                    insertName = "14_871";
                    break;
                case "ldb028":
                    insertName = "14_872";
                    break;
                case "ldb079":
                    insertName = "14_873";
                    break;
                case "ldb038":
                    insertName = "14_874";
                    break;
                case "ldb061":
                    insertName = "14_875";
                    break;
                case "ldb036":
                    insertName = "14_876";
                    break;
                case "ldb062":
                    insertName = "14_877";
                    break;
                case "ldb023":
                    insertName = "14_878";
                    break;
                case "ldb007":
                    insertName = "14_870";
                    break;
                case "ldb052":
                    insertName = "14_869";
                    break;
                case "ldb050":
                    insertName = "14_861";
                    break;


                case "ldb032":
                    insertName = "14_862";
                    break;
                case "ldb016":
                    insertName = "14_863";
                    break;
                case "ldb082":
                    insertName = "14_864";
                    break;
                case "ldb039":
                    insertName = "14_865";
                    break;
                case "ldb043":
                    insertName = "14_866";
                    break;
                case "ldb019":
                    insertName = "14_867";
                    break;
                case "ldb060":
                    insertName = "14_868";
                    break;
                case "ldb046":
                    insertName = "14_879";
                    break;
                case "ldb002":
                    insertName = "14_880";
                    break;
                case "ldb055":
                    insertName = "14_881";
                    break;
                case "ldb080":
                    insertName = "14_882";
                    break;
                case "ldb073":
                    insertName = "14_883";
                    break;


                case "ldb040":
                    insertName = "14_894";
                    break;
                case "ldb020":
                    insertName = "14_895";
                    break;
                case "ldb008":
                    insertName = "14_896";
                    break;
                case "ldb024":
                    insertName = "14_897";
                    break;
                case "ldb042":
                    insertName = "14_898";
                    break;
                case "ldb018":
                    insertName = "14_899";
                    break;

                case "ldb034":
                    insertName = "14_891";
                    break;
                case "ldb041":
                    insertName = "14_890";
                    break;
                case "ldb083":
                    insertName = "14_882";
                    break;
                case "ldb067":
                    insertName = "14_883";
                    break;
                case "ldb025":
                    insertName = "14_884";
                    break;
                case "ldb063":
                    insertName = "14_885";
                    break;


                case "ldb078":
                    insertName = "14_886";
                    break;
                case "ldb065":
                    insertName = "14_887";
                    break;
                case "ldb004":
                    insertName = "14_888";
                    break;
                case "ldb081":
                    insertName = "14_889";
                    break;
                case "ldb006":
                    insertName = "14_900";
                    break;
                case "ldb026":
                    insertName = "14_859";
                    break;
                case "ldb017":
                    insertName = "14_818";
                    break;
                case "ldb011":
                    insertName = "14_829";
                    break;
                case "ldb014":
                    insertName = "14_830";
                    break;
                case "ldb070":
                    insertName = "14_831";
                    break;
                case "ldb056":
                    insertName = "14_832";
                    break;
                case "ldb021":
                    insertName = "14_833";
                    break;


                case "ldb010":
                    insertName = "14_834";
                    break;
                case "ldb045":
                    insertName = "14_835";
                    break;
                case "ldb076":
                    insertName = "14_836";
                    break;
                case "ldb005":
                    insertName = "14_828";
                    break;
                case "ldb069":
                    insertName = "14_827";
                    break;
                case "ldb030":
                    insertName = "14_819";
                    break;
                case "ldb071":
                    insertName = "14_820";
                    break;
                case "ldb044":
                    insertName = "14_821";
                    break;
                case "ldb027":
                    insertName = "14_822";
                    break;
                case "ldb013":
                    insertName = "14_823";
                    break;
                case "ldb015":
                    insertName = "14_824";
                    break;
                case "ldb072":
                    insertName = "14_825";
                    break;


                case "ldb054":
                    insertName = "14_826";
                    break;
                case "ldb049":
                    insertName = "14_837";
                    break;
                case "ldb068":
                    insertName = "14_838";
                    break;
                case "ldb059":
                    insertName = "14_839";
                    break;
                case "ldb075":
                    insertName = "14_850";
                    break;
                case "ldb031":
                    insertName = "14_851";
                    break;
                case "ldb064":
                    insertName = "14_852";
                    break;
                case "ldb037":
                    insertName = "14_853";
                    break;
                case "ldb003":
                    insertName = "14_854";
                    break;
                case "ldb012":
                    insertName = "14_855";
                    break;
                case "ldb035":
                    insertName = "14_856";
                    break;
                case "ldb022":
                    insertName = "14_857";
                    break;


                case "ldb001":
                    insertName = "14_849";
                    break;
                case "ldb057":
                    insertName = "14_848";
                    break;
                case "ldb048":
                    insertName = "14_840";
                    break;
                case "ldb077":
                    insertName = "14_841";
                    break;
                case "ldb009":
                    insertName = "14_842";
                    break;
                case "ldb053":
                    insertName = "14_843";
                    break;
                case "ldb029":
                    insertName = "14_844";
                    break;
                case "ldb047":
                    insertName = "14_845";
                    break;
                case "ldb066":
                    insertName = "14_846";
                    break;
                case "ldb051":
                    insertName = "14_847";
                    break;
                case "ldb058":
                    insertName = "14_858";
                    break;

            }
        } else if (smiley_type == SMILEY_ACN) {
            switch (name) {

                case "acn062":
                    insertName = "15_950";
                    break;

                case "acn006":
                    insertName = "15_963";
                    break;
                case "acn025":
                    insertName = "15_964";
                    break;
                case "acn035":
                    insertName = "15_965";
                    break;
                case "acn071":
                    insertName = "15_966";
                    break;
                case "acn037":
                    insertName = "15_967";
                    break;
                case "acn017":
                    insertName = "15_968";
                    break;
                case "acn047":
                    insertName = "15_969";
                    break;
                case "acn016":
                    insertName = "15_970";
                    break;
                case "acn009":
                    insertName = "15_971";
                    break;
                case "acn010":
                    insertName = "15_972";
                    break;
                case "acn020":
                    insertName = "15_962";
                    break;


                case "acn072":
                    insertName = "15_961";
                    break;
                case "acn060":
                    insertName = "15_951";
                    break;
                case "acn065":
                    insertName = "15_952";
                    break;
                case "acn070":
                    insertName = "15_953";
                    break;
                case "acn032":
                    insertName = "15_954";
                    break;
                case "acn078":
                    insertName = "15_955";
                    break;
                case "acn049":
                    insertName = "15_956";
                    break;
                case "acn013":
                    insertName = "15_957";
                    break;
                case "acn061":
                    insertName = "15_958";
                    break;
                case "acn064":
                    insertName = "15_959";
                    break;
                case "acn024":
                    insertName = "15_960";
                    break;
                case "acn093":
                    insertName = "15_973";
                    break;

                case "acn036":
                    insertName = "15_974";
                    break;
                case "acn083":
                    insertName = "15_987";
                    break;
                case "acn095":
                    insertName = "15_988";
                    break;
                case "acn054":
                    insertName = "15_989";
                    break;
                case "acn069":
                    insertName = "15_990";
                    break;
                case "acn074":
                    insertName = "15_991";
                    break;
                case "acn043":
                    insertName = "15_992";
                    break;
                case "acn039":
                    insertName = "15_993";
                    break;
                case "acn089":
                    insertName = "15_994";
                    break;
                case "acn045":
                    insertName = "15_995";
                    break;
                case "acn030":
                    insertName = "15_996";
                    break;
                case "acn005":
                    insertName = "15_986";
                    break;

                case "acn018":
                    insertName = "15_985";
                    break;
                case "acn076":
                    insertName = "15_975";
                    break;
                case "acn079":
                    insertName = "15_976";
                    break;
                case "acn008":
                    insertName = "15_977";
                    break;
                case "acn033":
                    insertName = "15_978";
                    break;
                case "acn077":
                    insertName = "15_979";
                    break;
                case "acn014":
                    insertName = "15_980";
                    break;
                case "acn001":
                    insertName = "15_981";
                    break;
                case "acn022":
                    insertName = "15_982";
                    break;
                case "acn096":
                    insertName = "15_983";
                    break;
                case "acn026":
                    insertName = "15_984";
                    break;
                case "acn084":
                    insertName = "15_997";
                    break;

                case "acn081":
                    insertName = "15_949";
                    break;
                case "acn055":
                    insertName = "15_901";
                    break;
                case "acn050":
                    insertName = "15_914";
                    break;
                case "acn091":
                    insertName = "15_915";
                    break;
                case "acn031":
                    insertName = "15_916";
                    break;
                case "acn002":
                    insertName = "15_917";
                    break;
                case "acn059":
                    insertName = "15_918";
                    break;
                case "acn073":
                    insertName = "15_919";
                    break;
                case "acn075":
                    insertName = "15_920";
                    break;
                case "acn046":
                    insertName = "15_921";
                    break;
                case "acn097":
                    insertName = "15_922";
                    break;
                case "acn038":
                    insertName = "15_923";
                    break;


                case "acn068":
                    insertName = "15_913";
                    break;
                case "acn044":
                    insertName = "15_912";
                    break;
                case "acn092":
                    insertName = "15_902";
                    break;
                case "acn028":
                    insertName = "15_903";
                    break;
                case "acn011":
                    insertName = "15_904";
                    break;
                case "acn087":
                    insertName = "15_905";
                    break;
                case "acn085":
                    insertName = "15_906";
                    break;
                case "acn057":
                    insertName = "15_907";
                    break;
                case "acn052":
                    insertName = "15_908";
                    break;
                case "acn090":
                    insertName = "15_909";
                    break;
                case "acn088":
                    insertName = "15_910";
                    break;
                case "acn080":
                    insertName = "15_911";
                    break;

                case "acn004":
                    insertName = "15_924";
                    break;
                case "acn053":
                    insertName = "15_925";
                    break;
                case "acn007":
                    insertName = "15_938";
                    break;
                case "acn003":
                    insertName = "15_939";
                    break;
                case "acn029":
                    insertName = "15_940";
                    break;
                case "acn040":
                    insertName = "15_941";
                    break;
                case "acn023":
                    insertName = "15_942";
                    break;
                case "acn058":
                    insertName = "15_943";
                    break;
                case "acn042":
                    insertName = "15_944";
                    break;
                case "acn067":
                    insertName = "15_945";
                    break;
                case "acn094":
                    insertName = "15_946";
                    break;
                case "acn048":
                    insertName = "15_947";
                    break;


                case "acn027":
                    insertName = "15_937";
                    break;
                case "acn066":
                    insertName = "15_936";
                    break;
                case "acn082":
                    insertName = "15_926";
                    break;
                case "acn051":
                    insertName = "15_927";
                    break;
                case "acn034":
                    insertName = "15_928";
                    break;
                case "acn012":
                    insertName = "15_929";
                    break;
                case "acn086":
                    insertName = "15_930";
                    break;
                case "acn056":
                    insertName = "15_931";
                    break;
                case "acn019":
                    insertName = "15_932";
                    break;
                case "acn015":
                    insertName = "15_933";
                    break;
                case "acn021":
                    insertName = "15_934";
                    break;
                case "acn041":
                    insertName = "15_935";
                    break;
                case "acn063":
                    insertName = "15_948";
                    break;
            }
        }

        if(listener!=null){
            listener.itemClick(insertName,ds.get(position));
        }

    }
}