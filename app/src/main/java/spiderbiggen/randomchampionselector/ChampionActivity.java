package spiderbiggen.randomchampionselector;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ChampionActivity extends Activity {

    private Champion champ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion);
        champ = Champions.pickRandomChampion(champ);
        populatePage();

    }


    private void populatePage() {

        TextView chName = (TextView) findViewById(R.id.nameValue);
        chName.setText(champ.NAME);
        TextView role = (TextView) findViewById(R.id.roleValue);
        role.setText(champ.ROLE);
        TextView health = (TextView) findViewById(R.id.healthValue);
        health.setText(champ.HEALTH + "");
        if (champ.RESOURCE != 0 && champ.RESOURCE_TYPE != null) {
            TextView resource = (TextView) findViewById(R.id.resourceText);
            resource.setText(champ.RESOURCE_TYPE);
            TextView resourceValue = (TextView) findViewById(R.id.resourceValue);
            resourceValue.setText(champ.RESOURCE + "");
        }
        TextView range = (TextView) findViewById(R.id.rangeValue);
        range.setText(champ.RANGE + "");
        TextView movSpeed = (TextView) findViewById(R.id.movSpeedValue);
        movSpeed.setText(champ.MOVEMENT_SPEED + "");

        RelativeLayout grid = (RelativeLayout) findViewById(R.id.gridView);
        Drawable image = champ.IMAGE;
        image.setAlpha(100);
        grid.setBackground(image);
        Runtime r = Runtime.getRuntime();
        r.gc();
    }

    public void pickRandomChampion(View view){
        champ = Champions.pickRandomChampion(champ);
        populatePage();
    }
}
