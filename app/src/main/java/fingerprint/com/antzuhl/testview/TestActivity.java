package fingerprint.com.antzuhl.testview;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import fingerprint.com.antzuhl.R;

/**
 * Created by wber on 18-12-8.
 */

public class TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.hello);
    }

}
