package tokyo.mp015v.mycamera

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class MyCanvas(context : Context, attr : AttributeSet) : View( context,attr) {
    val mainPaint = Paint()
    val rectPaint = Paint()
    val textPaint = Paint()
    val rectPoint :ArrayList<RectPoint> = arrayListOf()
    val textPoint : ArrayList<TextPoint> = arrayListOf()
    lateinit var bitmap:Bitmap
    var flg = 0

    override fun onDraw(canvas: Canvas){
        //ペイントの設定
        mainPaint.apply{
            setColor(Color.argb(255, 0, 0, 0))
        }
        rectPaint.apply {
            setColor(Color.argb(125, 255, 255, 0))
            //strokeWidth = 5.0F
            //style = Paint.Style.STROKE
        }

        textPaint.apply{
            setColor( Color.argb(255,255,0,0))
            textSize = 50f

        }
        if( flg == 0) {
            //描画クリア
            //canvas.drawColor(0, PorterDuff.Mode.CLEAR)
            canvas.drawColor( Color.argb(255,255,255,255))
            //bitmap表示
            canvas.drawBitmap(bitmap,0F,0F,mainPaint)
        }else if( flg == 1 ){
            canvas.drawBitmap(bitmap,0F,0F,mainPaint)
            //矩形描画
            rectPoint.forEach{
                canvas.drawRect( it.p1.x.toFloat() ,it.p1.y.toFloat() ,it.p2.x.toFloat() ,it.p2.y.toFloat() ,rectPaint )
            }
            textPoint.forEach{
                canvas.drawText( it.text,it.p.x.toFloat() ,it.p.y.toFloat() ,textPaint )
            }

        }
    }

    inner class RectPoint( p1 : Point , p2 : Point){
        val p1 = p1
        val p2 = p2
    }
    inner class TextPoint( text : String , p : Point ){
        val text = text
        val p = p
    }
    fun addRectPoint( x1:Int , y1:Int ,x2:Int,y2:Int){
        rectPoint.add( RectPoint( Point(x1,y1),Point(x2,y2)))
    }
    fun addTextPoint( text:String , x:Int,y:Int){

        textPoint.add( TextPoint( text , Point(x,y)))

    }
    fun showCanvas( ){
        flg = 1
        invalidate( )
    }

    fun showCanvas(bitmap:Bitmap){
        this.bitmap = bitmap
        flg = 0
        invalidate()
    }
}