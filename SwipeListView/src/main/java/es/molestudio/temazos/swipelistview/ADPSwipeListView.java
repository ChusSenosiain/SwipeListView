package es.molestudio.temazos.swipelistview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;


public class ADPSwipeListView extends BaseAdapter{

    private ArrayList<Item> listItems;

    /** Animacion del botón hacia la izquierda */
    private Animation btnToLeft = null;
    /** Animactión del botón hacia la derecha */
    private Animation btnToRigth = null;
    /** Animación para dar el efecto pulsado sobre la celda */
    private Animation celdaPush  = null;
    /** Animación para desplazar la celda hacia la izquierda */
    //private TranslateAnimation celdaToLeft = null;
    private Animation celdaToLeft = null;
    /** Animación para desplazar la celda hacia la derecha */
    //private TranslateAnimation celdaToRigth = null;
    private Animation celdaToRigth = null;
    /** Detector de gesto swipe */
    private SwipeDetector mSwipeDetector = null;
    /** Número de píxeles que se mueve la celda par a que aparezca el botón de borrado */
    private int translatePixels = 0;
    private Context mContext = null;
    /** Posicion pulsado */
    private int posPulsado = 0;
    /** ClickListener personalizado */
    private View.OnClickListener mClicklistener = null;

    /** Carga la vista de la celda */
    LayoutInflater inflater = null;


    public ADPSwipeListView(Context _context){

        mContext = _context;

        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Cargo los items a mostrar en el listview
        this.listItems = loadItems();


        btnToLeft = AnimationUtils.loadAnimation(mContext, R.anim.btnrigth2left);
        btnToRigth = AnimationUtils.loadAnimation(mContext, R.anim.btnleft2rigth);
        celdaPush = AnimationUtils.loadAnimation(mContext, R.anim.scale_down);

        celdaPush.setAnimationListener(new myAnimationListener());

        // Detector del movimiento sobre la celda
        mSwipeDetector = new SwipeDetector();

        // Numero de dp (convertido a pixeles) para mover la celda y el botón hacía la
        // izquierda o derecha si se produce un evento de tipo swipe
        translatePixels = getPx(100);

        // Detector del evento click sobre la celda
        mClicklistener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final ADPSwipeListView.ViewHolder holder = (ADPSwipeListView.ViewHolder) view.getTag(R.layout.listitem);
                final int position = (Integer) view.getTag();

                // Se detecta el swipe
                if(mSwipeDetector.swipeDetected()) {
                    // Movimiento de derecha a izquierda
                    if(mSwipeDetector.getAction() == SwipeDetector.Action.RL) {
                        if (!holder.btn_remove.isShown()) {
                            showButton(holder);
                        }
                    // Movimiento hacia la derecha
                    } else if (mSwipeDetector.getAction() == SwipeDetector.Action.LR) {
                        if (holder.btn_remove.isShown()) {
                            hideButton(holder);
                        }
                    }
                // Si no se detecta ningún gesto se trata de un click
                } else {

                    if (holder.btn_remove.isShown()) {
                        hideButton(holder);
                    }
                    // Al finalizar la animación se llama a la ventana de view
                    posPulsado = position;
                    holder.celda.startAnimation(celdaPush);
                }
            }
        };
    }



    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /** Elimina el item del adaptador y de la vista */
    public void deleteItem(int _position){
        try {
            listItems.remove(_position);
            this.notifyDataSetChanged();
        } catch (Exception e) {}

    }

    /**
     * Clase que contiene la celda y el botón para poder realizar
     * operaciones con ella
     */
    class ViewHolder{
        MyButton btn_remove;
        RelativeLayout celda;
    }


    /**
     * Obtiene la vista para cargar en el listado de canciones
     */
    @Override
    public View getView(final int position, View contentView, ViewGroup viewGroup) {

        View mView = contentView;
        ViewHolder mHolder = new ViewHolder();

        if(contentView == null) {
            mView = inflater.inflate(R.layout.listitem, null);
            mView.setTag(R.layout.listitem, mHolder);

        } else{
            mView = contentView;
            mHolder = (ViewHolder) mView.getTag(R.layout.listitem);
        }

        final Item mItem = listItems.get(position);

        TextView tvArtist  = (TextView) mView.findViewById(R.id.tvArtist);
        tvArtist.setText(mItem.artist);

        TextView tvSong = (TextView) mView.findViewById(R.id.tvSong);
        tvSong.setText(mItem.song);


        // Obtengo la imagen para el thumbnail de los drawables
        ImageView songImage = (ImageView) mView.findViewById(R.id.ivImage);
        try {
            final int resourceId = mContext.getResources().getIdentifier(mItem.thumbnail, "drawable",
                    mContext.getPackageName());

            final Drawable resImg = mContext.getResources().getDrawable(resourceId);
            final Bitmap thumbImage = ThumbnailUtils.extractThumbnail(((BitmapDrawable)resImg).getBitmap(),
                                                                96,96);
            songImage.setImageBitmap(getRoundedCornerBitmap(thumbImage, getPx(3)));

        } catch (Exception e) {}


        mView.setTag(position);
        mView.setOnTouchListener(mSwipeDetector);
        mView.setOnClickListener(mClicklistener);

        mHolder.btn_remove = (MyButton) mView.findViewById(R.id.btn_remove);
        mHolder.celda = (RelativeLayout) mView.findViewById(R.id.celda);

        mHolder.btn_remove.setTag(position);
        mHolder.btn_remove.setOnClickListener(new MyClickListener(mHolder));


        return mView;
    }


    /**
     * Carga de items
     */
    private ArrayList<Item> loadItems(){

        ArrayList<Item> items = new ArrayList<Item>();

        items.add(new Item(1, "Should Be Higher", "Depeche Mode", "depeche"));
        items.add(new Item(2, "Princess of China", "Coldplay", "coldplay"));
        items.add(new Item(3, "Don't sit down cause i've moved your chair", "Arctic Monkeys", "arctic"));

        return items;
    }


    /**
     * Escuchador para el evento click del botón borrar
     * Si se produce el evento click sobre el botón eliminar
     * pide confirmación de borrado de la escena
     */
    private class MyClickListener implements View.OnClickListener {

        private ViewHolder mHolder;
        private int pos;

        public MyClickListener(ViewHolder _holder) {

            mHolder = _holder;
            pos = (Integer) mHolder.btn_remove.getTag();
        }

        @Override
        public void onClick(final View v) {
            hideButton(mHolder);
            deleteItem(pos);
        }
    }

    /**
     * Oculta el botón de borrado con la animación hacia la derecha
     * @param _holder clase que contiene las vistas a animar
     */
    public void hideButton(final ViewHolder _holder) {

        celdaToRigth = null;
        celdaToRigth = new TranslateAnimation(-translatePixels, 0f, 0f, 0f);
        celdaToRigth.setFillAfter(true);
        celdaToRigth.setDuration(150);



        _holder.btn_remove.startAnimation(btnToRigth);
        _holder.celda.startAnimation(celdaToRigth);
        _holder.btn_remove.postDelayed(new Runnable() {
            @Override
            public void run() {
                _holder.btn_remove.setVisibility(View.GONE);
            }
        }, 150);

    }


    /**
     * Muestra el botón de borrado con una animación hacia la izquierda
     * @param _holder clase que contiene las vistas a animar
     */
    private void showButton(final ViewHolder _holder) {

        celdaToLeft = null;
        celdaToLeft = new TranslateAnimation(0f, -translatePixels, 0f, 0f);
        celdaToLeft.setFillAfter(true);
        celdaToLeft.setDuration(150);

        _holder.btn_remove.setVisibility(View.VISIBLE);
        _holder.celda.startAnimation(celdaToLeft);
        _holder.btn_remove.startAnimation(btnToLeft);


    }


    /**
     * Escuchador para las animaciones: cuando la animación termina indica el item que
     * se ha seleccionado en un mensaje por pantalla.
     */
    private class myAnimationListener implements Animation.AnimationListener {

        public myAnimationListener () {

        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

            Item itemSelected = listItems.get(posPulsado);
            Toast.makeText(mContext, "Has pulsado el item: " + itemSelected.song, Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }


    /**
     * Convertir dp a pixels
     * @param _dimensionDp
     * @return
     */
    public int getPx(int _dimensionDp) {
        float density = mContext.getResources().getDisplayMetrics().density;
        return (int) (_dimensionDp * density + 0.5f);
    }

    /**
     * Creación de un bitmap con los bordes redondeados
     * @param _bitmapSrc
     * @param _roundPixels
     * @return
     */
    private Bitmap getRoundedCornerBitmap(Bitmap _bitmapSrc, int _roundPixels)  {

        Bitmap output = Bitmap.createBitmap(_bitmapSrc.getWidth(), _bitmapSrc
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, _bitmapSrc.getWidth(), _bitmapSrc.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = _roundPixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(_bitmapSrc, rect, rect, paint);

        return output;
    }



    public class Item implements Serializable {

        public Integer id;
        public String song;
        public String artist;
        public String thumbnail;


        public Item(Integer _id, String _song, String _artist, String _thumbnail){
            super();
            this.id = _id;
            this.song = _song;
            this.artist = _artist;
            this.thumbnail = _thumbnail;

        }

    }



}
