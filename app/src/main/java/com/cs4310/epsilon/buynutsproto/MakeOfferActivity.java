package com.cs4310.epsilon.buynutsproto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cs4310.epsilon.nutsinterface.Commodity;
import com.cs4310.epsilon.nutsinterface.SellOffer;
import com.cs4310.epsilon.nutsinterface.UnitsWt;

/**
 * An activity that allows a user to create a new SellOffer object and send
 * it to the backend
 */
public class MakeOfferActivity extends AppCompatActivity {
    static final String TAG = "myTag";
    private long mUid;

    Spinner spinUnitWt;
    Spinner spinCommodityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_offer);

        //get mUid from intent
        mUid = this.getIntent().getLongExtra("mUid", MainLoginActivity.INVALID_USERID);
        Log.i(TAG, (mUid == MainLoginActivity.INVALID_USERID ? "Didn't receive mUid" : "Received mUid=" + mUid));

        spinUnitWt = (Spinner) findViewById(R.id.spinnerWeightUnits_MO);
        FillSpinner.fill(this, R.array.wt_units_array, spinUnitWt);

        spinCommodityType = (Spinner) findViewById(R.id.spinnerCommodityType_MO);
        FillSpinner.fill(this, R.array.commodities_array, spinCommodityType);


        // Set onclickListener
        Button btnMakeOffer = (Button) findViewById(R.id.btnMakeOffer_MO);
        btnMakeOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create SellOffer
                SellOffer newOffer = getSellOfferFromUI(); // new SellOffer(mUid, ppu, minWt, maxWt, terms, cType, unitsWeight);


                // Send SellOffer object to the server

                Toast.makeText(MakeOfferActivity.this, "SellOffer is: " + newOffer, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Reads all the input fields in the UI and generates a new SellOffer
     * object. Also performs input validation and returns null for invalid
     * offers.
     * @return  A new SellOffer object made from UI input fields. If the
     *          SellOffer is invalid in some way, returns null.
     */
    SellOffer getSellOfferFromUI() {
        // set default (invalid) values
        double ppu=-1, minWt=-1, maxWt=-1;
        try {
            // get numeric data from the UI
            EditText etPPU = (EditText) findViewById(R.id.etPPU_MO);
            ppu = Double.parseDouble(etPPU.getText().toString().trim());

            EditText etMinWeight = (EditText) findViewById(R.id.etMinWeight_MO);
            minWt = Double.parseDouble(etMinWeight.getText().toString().trim());

            EditText etMaxWeight = (EditText) findViewById(R.id.etMaxWeight_MO);
            maxWt = Double.parseDouble(etMaxWeight.getText().toString().trim());
        } catch (NumberFormatException e){
            // if the user entered something that wasn't a number, we come here
            Toast.makeText(MakeOfferActivity.this.getApplicationContext(),
                    "Non-numeric input", Toast.LENGTH_SHORT).show();
            return null;
        }
        // If the numeric input doesn't make sense, don't make a SellOffer
        if (ppu < 0 || minWt < 0 || maxWt < minWt) {
            Toast.makeText(MakeOfferActivity.this.getApplicationContext(),
                    "Invalid numeric input", Toast.LENGTH_SHORT).show();
            return null;
        }

        // get string data from the UI
        EditText etTerms = (EditText)findViewById(R.id.etTerms_MO);
        String terms = etTerms.getText().toString().trim();

        //get commodity type and weight units
        Commodity.Type cType = Commodity.toType(
                spinCommodityType.getSelectedItem().toString());
        UnitsWt.Type unitsWeight = UnitsWt.toType(
                spinUnitWt.getSelectedItem().toString());

        // check that neither inputs from the spinner objects were invalid.
        // I don't think it's possible to get invalid input, given the way
        // these are set up, but this will help if something breaks
        if(cType == null || unitsWeight == null){
            Toast.makeText(MakeOfferActivity.this.getApplicationContext(),
                    "Invalid spinner input", Toast.LENGTH_SHORT).show();
            return null;
        }
        //make the new SellOffer and return it
        return new SellOffer(mUid, ppu, minWt, maxWt, terms, cType, unitsWeight);
    }
}
