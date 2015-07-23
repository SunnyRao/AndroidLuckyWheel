package com.sunnyrao.luckywheel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class LuckyWheel extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {

	private SurfaceHolder mHolder;
	private Canvas mCanvas;
	/**
	 * ���ڻ��Ƶ��߳�
	 */
	private Thread t;
	/**
	 * �̵߳Ŀ��ƿ���
	 */
	private boolean isRunning;
	/**
	 * ת�̽���
	 */
	private String[] mStrs = new String[] { "�������", "IPAD", "��ϲ����", "IPHONE",
			"��װһ��", "��ϲ����" };
	/**
	 * ת��ͼƬ
	 */
	private int[] mImgs = new int[] { R.drawable.p_camera, R.drawable.p_ipad,
			R.drawable.f015, R.drawable.p_iphone, R.drawable.p_girl,
			R.drawable.f040 };
	/**
	 * ��ͼƬ��Ӧ��bitmap����
	 */
	private Bitmap[] mImgsBitmap;

	private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.bg2);
	/**
	 * �̿����ɫ
	 */
	private int[] mColors = new int[] { 0xffffc300, 0xfff17e01, 0xffffc300,
			0xfff17e01, 0xffffc300, 0xfff17e01 };
	/**
	 * �����̿�Ļ���
	 */
	private Paint mArcPaint;
	/**
	 * �����ı��Ļ���
	 */
	private Paint mTextPaint;

	private float mTextSize = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());

	private int mItemCount = 6;
	/**
	 * ת�̵ķ�Χ
	 */
	private RectF mRange = new RectF();
	/**
	 * ת�̵İ뾶
	 */
	private int mRadius;
	/**
	 * ת�̵�����λ��
	 */
	private int mCenter;
	/**
	 * ��paddingleftΪ׼
	 */
	private int mPadding;
	/**
	 * �������ٶ�
	 */
	private double mSpeed;
	/**
	 * ��ʼ�ĽǶ�
	 */
	private volatile float mStartAngle = 0;
	/**
	 * �ж��Ƿ�����ֹͣ��ť
	 */
	private boolean isShouldEnd;

	public LuckyWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);
		// �ɻ�ý���
		setFocusable(true);
		setFocusableInTouchMode(true);
		// ���ó���
		setKeepScreenOn(true);
	}

	public LuckyWheel(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = Math.min(getMeasuredHeight(), getMeasuredWidth());
		mPadding = getPaddingLeft();
		// �뾶
		mRadius = (width - mPadding * 2) / 2;
		// ���ĵ�
		mCenter = width / 2;

		setMeasuredDimension(width, width);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// ��ʼ�������̿�Ļ���
		mArcPaint = new Paint();
		mArcPaint.setAntiAlias(true);
		mArcPaint.setDither(true);
		// ��ʼ�������ı��Ļ���
		mTextPaint = new Paint();
		mTextPaint.setColor(0xffffffff);
		mTextPaint.setTextSize(mTextSize);
		// ��ʼ���̿���Ʒ�Χ
		mRange = new RectF(mPadding, mPadding, mPadding + mRadius * 2, mPadding
				+ mRadius * 2);
		// ��ʼ��ͼƬ
		mImgsBitmap = new Bitmap[mItemCount];
		for (int i = 0; i < mItemCount; i++) {
			mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(),
					mImgs[i]);
		}

		isRunning = true;
		t = new Thread(this);
		t.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning = false;
	}

	@Override
	public void run() {
		// ÿ50ms����һ��
		while (isRunning) {
			long start = System.currentTimeMillis();
			draw();
			long end = System.currentTimeMillis();
			if (end - start < 50) {
				try {
					Thread.sleep(50 - (end - start));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void draw() {
		try {
			mCanvas = mHolder.lockCanvas();
			if (mCanvas != null) {
				// ���Ʊ���
				drawBackground();
				// �����̿�
				float tmpAngle = mStartAngle;
				float sweepAngle = 360 / mItemCount;
				for (int i = 0; i < mItemCount; i++) {
					mArcPaint.setColor(mColors[i]);
					// �����̿�
					mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true,
							mArcPaint);
					// �����ı�
					drawText(tmpAngle, sweepAngle, mStrs[i]);
					// ����Icon
					drawIcon(tmpAngle, mImgsBitmap[i]);

					tmpAngle += sweepAngle;
				}
				mStartAngle += mSpeed;
				// ��������ֹͣ��ť����ת�̻���ͣ��
				if (isShouldEnd) {
					mSpeed -= 1;
				}
				if (mSpeed <= 0) {
					mSpeed = 0;
					isShouldEnd = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCanvas != null) {
				mHolder.unlockCanvasAndPost(mCanvas);
			}
		}
	}

	/**
	 * ����Icon
	 */
	private void drawIcon(float tmpAngle, Bitmap bitmap) {
		// ����ͼƬ�Ŀ��Ϊ�뾶��1/4
		int imgWidth = mRadius / 4;
		float angle = (float) ((tmpAngle + 360 / mItemCount / 2) * Math.PI / 180);
		int x = (int) (mCenter + mRadius / 2 * Math.cos(angle));
		int y = (int) (mCenter + mRadius / 2 * Math.sin(angle));
		// ȷ��ͼƬ��λ��
		Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth
				/ 2, y + imgWidth / 2);
		mCanvas.drawBitmap(bitmap, null, rect, null);
	}

	/**
	 * ����ÿ���̿���ı�
	 */
	private void drawText(float tmpAngle, float sweepAngle, String string) {
		Path path = new Path();
		path.addArc(mRange, tmpAngle, sweepAngle);
		// ����ˮƽƫ���������־���
		float textWidth = mTextPaint.measureText(string);
		int hOffset = (int) (mRadius * Math.PI / mItemCount - textWidth / 2);
		// ��ֱƫ����
		int vOffset = mRadius / 6;
		mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
	}

	/**
	 * ���Ʊ���
	 */
	private void drawBackground() {
		mCanvas.drawColor(0xffffffff);
		mCanvas.drawBitmap(mBgBitmap, null, new Rect(mPadding / 2,
				mPadding / 2, getMeasuredWidth() - mPadding / 2,
				getMeasuredHeight() - mPadding / 2), null);
	}

	/**
	 * ������ת
	 * 
	 * @param index
	 *            ָ��ͣ������λ��
	 */
	public void luckyStart(int index) {
		if (index >= 0 && index < mItemCount) {
			// ����ÿһ��ĽǶ�
			float angle = 360 / mItemCount;
			// ����ÿһ���н���Χ
			float from = 270 - (index + 1) * angle;
			float end = from + angle;
			// ����ͣ������Ҫ��ת�ľ���
			float targetFrom = 5 * 360 + from;
			float targetEnd = 5 * 360 + end;
			/**
			 * ͣ�¹��� v1 -> 0 ÿ��-1
			 * 
			 * (v1+0)*(v1+1)/2=targetFrom v1*v1+v1-2*targetFrom=0
			 * 
			 * v1=(-1+Math.sqrt(1+8*targetFrom))/2;
			 */
			float v1 = (float) ((-1 + Math.sqrt(1 + 8 * targetFrom)) / 2);
			float v2 = (float) ((-1 + Math.sqrt(1 + 8 * targetEnd)) / 2);

			mSpeed = v1 + Math.random() * (v2 - v1);
		} else {
			mSpeed = 50;
		}
		isShouldEnd = false;
	}

	/**
	 * ֹͣ��ת
	 */
	public void luckyEnd() {
		isShouldEnd = true;
		mStartAngle = 0;
	}

	/**
	 * ת���Ƿ�����ת
	 */
	public boolean isStart() {
		return mSpeed != 0;
	}

	public boolean isShouldEnd() {
		return isShouldEnd;
	}
}
