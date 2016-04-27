package team8.personaltransportation;

import android.app.Application;
import android.app.Activity;
import android.test.ApplicationTestCase;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.os.Bundle;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends Activity {
//public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        //super(Application.class);
        //super(ApplicationTest.class);
        //super(ApplicationTestCase.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        configureImageButton();
    }
    private void configureImageButton(){
        /*ImageButton warning = (ImageButton) findViewById(R.id.warning);
        warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ApplicationTest.this,"Hazard Lights Activated!", Toast.LENGTH_LONG).show();
                Log.d("BUTTONS", "onClickListener reached here!");

                //Change Image on button
                ImageButton warning = (ImageButton) findViewById(R.id.warning);
                warning.setImageResource(R.drawable.warningoffnew);
            }
        });*/
    }
}