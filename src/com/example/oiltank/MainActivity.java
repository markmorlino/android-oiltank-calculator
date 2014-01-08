package com.example.oiltank;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	// number pickers for input values
	private NumberPicker nplength;
	private NumberPicker npwidth;
	private NumberPicker npheight;
	private NumberPicker npdepth;
	
	// textual representations of inputs, these can go away
	TextView tvlength;
	TextView tvwidth;
	TextView tvheight;
	TextView tvdepth;
	
	// textual representation of result
	TextView tvresult;
	
	// constant maximum value for number pickers
	private final int npmax = 99;
	
	//constant minimum value for number pickers
	private final int npmin = 1;
	
	// constant canvas size
	private final int screen = 320;
	
	// constant to scale inputs values for rendering on canvas
	private final int scale = 3;
	
	// drawing objects
	Bitmap bitmap;
	Canvas canvas;
	Paint paintblack;
	Paint paintwhite;
	Paint paintred;
	ImageView imageview;
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	// setup the number picker for tank length
        nplength = (NumberPicker) findViewById(R.id.nplength);
        nplength.setWrapSelectorWheel(false);
    	nplength.setMaxValue(npmax);
        nplength.setMinValue(npmin);
    	nplength.setValue(60);
        nplength.setOnValueChangedListener(new OnValueChangeListener() {
        	@Override
        	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        		somethingChanged();
        	}	
        });
        
        // setup the number picker for tank height
        npheight = (NumberPicker) findViewById(R.id.npheight);
        npheight.setWrapSelectorWheel(false);
        npheight.setMaxValue(npmax);
        npheight.setMinValue(npmin);
        npheight.setValue(45);
        npheight.setOnValueChangedListener(new OnValueChangeListener() {
        	@Override
        	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        		somethingChanged();
        	}	
        });
        
        // setup the number picker for tank width
        npwidth = (NumberPicker) findViewById(R.id.npwidth);
        npwidth.setWrapSelectorWheel(false);
        npwidth.setMinValue(npmin);
    	npwidth.setMaxValue(Math.min(npmax,npheight.getValue()));
    	npwidth.setValue(45);
        npwidth.setOnValueChangedListener(new OnValueChangeListener() {
        	@Override
        	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        		somethingChanged();
        	}	
        });
        
        // setup the number picker for tank depth
        npdepth = (NumberPicker) findViewById(R.id.npdepth);
        npdepth.setWrapSelectorWheel(false);
        npdepth.setMinValue(npmin);
        npdepth.setMaxValue(npheight.getValue());
        npdepth.setValue(npheight.getValue() / 2);
        npdepth.setOnValueChangedListener(new OnValueChangeListener() {
        	@Override
        	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        		somethingChanged();
        	}	
        });
        
        // setup the textviews - these can go away
        //tvlength = (TextView) findViewById(R.id.L);
        //tvwidth = (TextView) findViewById(R.id.W);
        //tvheight = (TextView) findViewById(R.id.H);
        //tvdepth = (TextView) findViewById(R.id.D);
        tvresult = (TextView) findViewById(R.id.textresult);
        
        // setup the drawing objects
        bitmap = Bitmap.createBitmap(screen,screen,Config.RGB_565);
        paintblack = new Paint();
        paintwhite = new Paint();
        paintred = new Paint();
        canvas = new Canvas(bitmap);
        imageview = (ImageView) findViewById(R.id.imageview);
        paintblack.setColor(Color.BLACK);
        paintwhite.setColor(Color.WHITE);
        paintred.setColor(Color.RED);
        canvas.drawRect(0, 0, screen, screen, paintblack);
    	imageview.setImageBitmap(bitmap);
        somethingChanged();
    }

    protected void somethingChanged(){
    	npwidth.setMaxValue(Math.min(npmax,npheight.getValue())); 	
    	npdepth.setMaxValue(npheight.getValue());  	
    	//tvlength.setText(Integer.toString(nplength.getValue()));
    	//tvheight.setText(Integer.toString(npheight.getValue()));
    	//tvwidth.setText(Integer.toString(npwidth.getValue()));
    	//tvdepth.setText(Integer.toString(npdepth.getValue()));
    	int length = nplength.getValue();
    	int height = npheight.getValue();
    	int width = npwidth.getValue();
    	int depth = npdepth.getValue();
    	
    	// conversion factor 1 gallon is 231 cubic inches
    	double conversion = 231.0;
    	int diameter = width;
    	double radius = diameter / 2.0;
    	int rectangleheight = height - width;
    	double depthforrectangle =0, depthforcircle;
    	if( depth <= radius) {
    		depthforrectangle = 0;
    		depthforcircle = radius - depth;
    	} else if( depth <= (radius + rectangleheight) ){
    		depthforrectangle = depth - radius;
    		depthforcircle = 0;
    	} else {
    		depthforrectangle = rectangleheight;
    		depthforcircle = depth - rectangleheight - radius;
    	}
    	double angle = 2 * Math.acos(depthforcircle / radius);
    	double segmentarea = Math.pow(radius,2) / 2 * (angle - Math.sin(angle));
    	double segmentvolume = segmentarea * length / conversion;
    	double rectanglevolume = depthforrectangle * width * length;
    	
    	double rectanglecapacity = rectangleheight * width * length / conversion;
    	double circlecapacity = Math.PI * Math.pow( radius, 2) * length / conversion;
    	double tankcapacity = rectanglecapacity + circlecapacity;
    	
    	double tankcontains;
    	if( depth <= radius ){
    		tankcontains = segmentvolume;
    	} else if( depth <= (radius + rectangleheight) ) {
    		tankcontains = segmentvolume + rectanglevolume;
    	} else {
    		tankcontains = tankcapacity - segmentvolume;
    	}
    	
    	tvresult.setText(String.format("Contains %.1f of %.1f gallons", tankcontains, tankcapacity));
    	// draw black over entire canves
    	canvas.drawRect(0, 0, screen, screen, paintblack);
    	RectF rectf = new RectF();
    	// fuel level line
    	double left = screen / 2 - radius * scale;
    	double top = screen / 2 - (radius + rectangleheight / 2 - depth) * scale;
    	double right = left + diameter * scale;
    	double bottom = screen;
    	//canvas.drawRect((float)left, (float)top, (float)right, (float)bottom, paintred);
    	
    	// top semicircle
    	top = screen / 2 - (radius + rectangleheight / 2) * scale;
    	bottom = top + diameter * scale;	
    	rectf.set((float)left,(float)top,(float)right,(float)bottom);
    	canvas.drawArc(rectf, 180, 180, false, paintwhite);
    	// middle rectangle
    	if( width != height){
    		top = screen / 2 - (rectangleheight / 2) * scale;
    		bottom = top + rectangleheight * scale;
    		canvas.drawRect((float)left, (float)top, (float)right, (float)bottom, paintwhite);
    	}
    	//bottom semicircle
    	top = screen / 2 - (radius - rectangleheight / 2) * scale;
    	bottom = top + diameter * scale;
    	rectf.set((float)left,(float)top,(float)right,(float)bottom);
    	canvas.drawArc(rectf, 0, 180, false, paintwhite);
    	
    	// fuel level line
    	top = bottom - depth * scale;
    	bottom = top + scale;
    	canvas.drawRect((float)left, (float)top, (float)right, (float)bottom, paintblack);
    	
    	// this redraws the image, apparently
    	imageview.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
