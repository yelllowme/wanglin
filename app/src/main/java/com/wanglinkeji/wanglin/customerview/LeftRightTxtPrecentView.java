package com.wanglinkeji.wanglin.customerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
public class LeftRightTxtPrecentView extends View {
	private Paint paint;//画笔
	private int txtLeftColor;//左边文字对应的圆环的颜色
	private int txtRightColor;//圆环进度的颜色
	private int borderColor;//边框圆环的颜色
	private int splitTxtColor=0xFFAFAFAF;//文字间分割文字颜色
	private float roundWidth;//圆环的宽度
	private float borderWidth;//边框圆环的宽度
	private float borderRoundGap;//边框圆环和内层圆环的间距	
	private float textSize;//圆环中心显示的文字的大小
	private float textSizePx;//圆环中心显示的文字的大小(px)
	private int max;//最大进度
	private int rightTxtProgress;//设置右边文字所占圆圈的比例
	private boolean textIsDisplayable;//是否显示中间的文字
	
	
	private int style;//进度的风格，实心或者空心
	public static final int STROKE = 0;
	public static final int FILL = 1;

	public LeftRightTxtPrecentView(Context context) {
		this(context, null);
	}

	public LeftRightTxtPrecentView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LeftRightTxtPrecentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		paint = new Paint();
		paint.setStyle(Paint.Style.STROKE); // 设置空心
		paint.setAntiAlias(true); // 消除锯齿
		paint.setAlpha(180);
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.LeftRightTxtPrecentView);

		// 获取自定义属性和默认值
		txtLeftColor = mTypedArray.getColor(
				R.styleable.LeftRightTxtPrecentView_txtLeftColor, Color.RED);//圆圈底色
		txtRightColor = mTypedArray.getColor(
				R.styleable.LeftRightTxtPrecentView_txtRightColor, Color.GREEN);//圆圈的进度颜色
		borderColor = mTypedArray.getColor(
				R.styleable.LeftRightTxtPrecentView_borderColor, Color.GRAY);//边框圆圈的颜色
		textSize = mTypedArray.getDimension(
				R.styleable.LeftRightTxtPrecentView_textSize, 15);//字体的大小
		textSizePx = DisplayUtil.sp2px(context, textSize);
		roundWidth = mTypedArray.getDimension(
				R.styleable.LeftRightTxtPrecentView_roundWidth, 10);//圆圈的宽度
		borderWidth = mTypedArray.getDimension(
				R.styleable.LeftRightTxtPrecentView_borderWidth, 4);//边框圆圈的宽度
		borderRoundGap =  mTypedArray.getDimension(
				R.styleable.LeftRightTxtPrecentView_borderRoundGap, 10);//边框圆环和内层圆环的间距	
		max = mTypedArray.getInteger(R.styleable.LeftRightTxtPrecentView_max, 100);//一圈代表的最大值
		textIsDisplayable = mTypedArray.getBoolean(
				R.styleable.LeftRightTxtPrecentView_textIsDisplayable, true);
		style = mTypedArray.getInt(R.styleable.LeftRightTxtPrecentView_style, 0);//圆圈的样式0-STROKE  1-FILL

		/**
		 * 回收TypedArray，以便后面重用。在调用这个函数后，你就不能再使用这个TypedArray。
		 * 在TypedArray后调用recycle主要是为了缓存。当recycle被调用后，
		 * 这就说明这个对象从现在可以被重用了。TypedArray 内部持有部分数组，
		 * 它们缓存在Resources类中的静态字段中，这样就不用每次使用前都需要分配内存
		 */
		mTypedArray.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		/**
		 * 画边框圆圈
		 */
		int halfWidth =getWidth()/2;
		paint.setColor(borderColor); // 
		paint.setStrokeWidth(borderWidth); // 设置圆环的宽度
		float borderRaidus = (halfWidth-roundWidth) + (roundWidth+borderWidth)/2 + borderRoundGap;
		canvas.drawCircle(halfWidth, halfWidth, borderRaidus, paint);
		/**
		 * 画最外层的大圆环
		 */
		paint.setColor(txtLeftColor); // 设置左侧圆圈颜色（先画出颜色是左侧的一个完整圆圈，右侧的再覆盖一部分）
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		canvas.drawCircle(halfWidth,halfWidth, halfWidth-roundWidth, paint);
		
		/**
		 * 画文字
		 */
		paint.setStrokeWidth(0);
		paint.setAlpha(255);
		paint.setTextSize(textSizePx);
		paint.setTypeface(Typeface.SERIF); // 设置字体
		float text1Width = paint.measureText("赞"); // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间
		float text2Width = paint.measureText("/"); // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间
		float text3Width = paint.measureText("踩"); // 测量字体宽度，我们需要根据字体的宽度设置在圆环中间
		if (textIsDisplayable && style == STROKE) {
			paint.setColor(splitTxtColor);
			canvas.drawText("/", halfWidth - text2Width / 2, halfWidth
					+ textSize / 2, paint); // 画出文字
			paint.setColor(txtLeftColor);
			canvas.drawText("赞", halfWidth - text2Width / 2-text1Width, halfWidth
					+ textSize / 2, paint); // 画出文字
			paint.setColor(txtRightColor);
			canvas.drawText("踩", halfWidth + text2Width / 2, halfWidth
					+ textSize / 2, paint); // 画出文字
		}
		/**
		 * 画右侧圆弧覆盖第一次画的整个圆圈
		 */
		int raidus =(int) (halfWidth-roundWidth);
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setStrokeCap(Paint.Cap.BUTT);//设置画笔的始末端与路径的末端一致
		paint.setColor(txtRightColor); // 设置进度的颜色
		RectF oval = new RectF(halfWidth-raidus, halfWidth-raidus , halfWidth+raidus, halfWidth+raidus); // 用于定义的圆弧的形状和大小的界限
		switch (style) {
		case STROKE: {
			paint.setStyle(Paint.Style.STROKE);
			drawArc(canvas, oval);
			break;
		}
		case FILL: {
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			drawArc(canvas, oval);
			break;
		}
		}

	}

	private void drawArc(Canvas canvas, RectF oval) {
		if(goalDegree>0){
			canvas.drawArc(oval, -90, goalDegree, false, paint); // 根据进度画圆弧
			//handler.sendEmptyMessage(88);
		}
	}
	private float goalDegree = 0;//设置的要显示的最终度数
	//private float curDegree = 0;//当前显示的度数
	/*private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			curDegree += 1;
			postInvalidate();//这里可以直接使用invalidate();方法。因为是通过Handler发的消息在UI线程中
		};
	};*/
	
	public synchronized int getMax() {
		return max;
	}

	/**
	 * 设置进度的最大值
	 * @param max
	 */
	public synchronized void setMax(int max) {
		if (max < 0) {
			throw new IllegalArgumentException("max not less than 0");
		}
		this.max = max;
	}

	/**
	 * 获取进度.需要同步
	 * @return
	 */
	public synchronized int getRightPrecent() {
		return rightTxtProgress;
	}

	/**
	 * 设置进度，此为线程安全控件，
	 * 由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
	 * 设置右侧文字所代表圆圈的比例
	 * @param rightPrecent
	 */
	public synchronized void setRightPrecent(int rightPrecent) {
		if (rightPrecent < 0) {
			throw new IllegalArgumentException("rightPrecent not less than 0");
		}
		if (rightPrecent > max) {
			rightPrecent = max;
		}
		if (rightPrecent <= max) {
			this.rightTxtProgress = rightPrecent;
			goalDegree = 360 * rightPrecent / max;
			//curDegree = 0;
			postInvalidate();
		}

	}

	public int getTxtLeftColor() {
		return txtLeftColor;
	}

	public void setTxtLeftColor(int txtLeftColor) {
		this.txtLeftColor = txtLeftColor;
	}

	public int getTxtRightColor() {
		return txtRightColor;
	}

	public void setTxtRightColor(int txtRightColor) {
		this.txtRightColor = txtRightColor;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public float getRoundWidth() {
		return roundWidth;
	}

	public void setRoundWidth(float roundWidth) {
		this.roundWidth = roundWidth;
	}

	public boolean isTextIsDisplayable() {
		return textIsDisplayable;
	}

	public void setTextIsDisplayable(boolean textIsDisplayable) {
		this.textIsDisplayable = textIsDisplayable;
	}

}
