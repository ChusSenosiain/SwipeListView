package es.molestudio.temazos.swipelistview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;


/**
 * Botón a medida que contienen el tipo de letra usado en la app
 * Created by chus on 02/04/2014.
 * @author María Jesús Senosiain
 */

public class MyButton extends Button {

    private Context mContext = null;

    public MyButton(Context context) {
        super(context);
        init(context);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(Context _context) {

        this.mContext = _context;

        if (!this.isInEditMode()) {
            Typeface type = Typeface.createFromAsset(mContext.getAssets(), "fonts/Arial Rounded Bold.ttf");
            this.setTypeface(type);
        }



    }



}
