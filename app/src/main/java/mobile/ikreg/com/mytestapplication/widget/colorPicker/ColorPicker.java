package mobile.ikreg.com.mytestapplication.widget.colorPicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ColorPicker extends View {

    /*
    *   This class and all its methods are copied, modified and merged from https://github.com/DASAR/ShiftColorPicker
    *   This project is subject to The MIT License (MIT) for software.
    *   The related copyright agreement can be found at https://opensource.org/licenses/MIT
    */

    private Rect rect = new Rect();
    private int[] colors = MaterialColorPalette.RAINBOW;
    private int cellSize;
    private int selectedColor = colors[0];
    private boolean isColorSelected;
    private OnColorChangedListener onColorChanged;
    private int screenW;
    Paint paint;

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawHorizontalPicker(canvas);
    }

    private void drawHorizontalPicker(Canvas canvas) {
        rect.left = 0;
        rect.top = 0;
        rect.right = 0;
        rect.bottom = canvas.getHeight();

        // 8% margin from canvas border
        int margin = Math.round(canvas.getHeight() * 0.08f);

        for (int i = 0; i < colors.length; i++ ) {
            paint.setColor(colors[i]);

            rect.left = rect.right;
            rect.right += cellSize;

            if(isColorSelected && colors[i] == selectedColor) {
                rect.top = 0;
                rect.bottom = canvas.getHeight();
            } else {
                rect.top = margin;
                rect.bottom = canvas.getHeight() - margin;
            }
            canvas.drawRect(rect, paint);
        }
    }

    private void onColorChanged(int color) {
        if(onColorChanged != null) onColorChanged.onColorChanged(color);
    }

    private boolean isClick = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int actionId = event.getAction();

        int newColor;

        switch (actionId) {
            case MotionEvent.ACTION_DOWN:
                isClick = true;
                break;
            case MotionEvent.ACTION_UP:
                newColor = getColorAtXY(event.getX(), event.getY());

                setSelectedColor(newColor);

                if (isClick) {
                    performClick();
                }

                break;

            case MotionEvent.ACTION_MOVE:
                newColor = getColorAtXY(event.getX(), event.getY());

                setSelectedColor(newColor);

                break;
            case MotionEvent.ACTION_CANCEL:
                isClick = false;
                break;

            case MotionEvent.ACTION_OUTSIDE:
                isClick = false;
                break;

            default:
                break;
        }

        return true;
    }

    private int getColorAtXY(float x, float y) {

        // FIXME: colors.length == 0 -> devision by ZERO.s
            int left = 0;
            int right = 0;

            for (int i = 0; i < colors.length; i++) {
                left = right;
                right += cellSize;

                if (left <= x && right >= x) return colors[i];
            }
            return selectedColor;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        screenW = w;

        recalcCellSize();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    private int recalcCellSize() {

//        if (mOrientation == HORIZONTAL) {
            cellSize = Math.round(screenW / (colors.length * 1f));
/*        } else {
            cellSize = Math.round(screenH / (colors.length * 1f));
        }
*/
        return cellSize;
    }

    public void setSelectedColor(int color) {

        // not from current palette
        if (!containsColor(colors, color)) return;

        // do we need to re-draw view?
        if (!isColorSelected || selectedColor != color) {
            this.selectedColor = color;

            isColorSelected = true;

            invalidate();

            onColorChanged(color);
        }
    }

    private boolean containsColor(int[] colors, int c) {
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == c)
                return true;
        }
        return false;
    }

    public void setOnColorChangedListener(OnColorChangedListener l) {
        this.onColorChanged = l;
    }

    public int getColor() {
        return selectedColor;
    }

    public String getHexColor() {
        return String.format("#%06X", 0xFFFFFF & selectedColor);
    }

    public void setColors(int[] colors) {
        // TODO: selected color can be NOT in set of colors
        // FIXME: colors can be null
        this.colors = colors;

        if (!containsColor(colors, selectedColor)) {
            selectedColor = colors[0];
        }

        recalcCellSize();

        invalidate();
    }
}
