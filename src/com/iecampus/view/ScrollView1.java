package com.iecampus.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

public class ScrollView1 extends ScrollView {
	private float xDistance, yDistance, xLast, yLast;
	// �϶��ľ��� size = 4 ����˼ ֻ�����϶���Ļ��1/4
	private static final int size = 4;
	private View inner;
	private float y;
	private Rect normal = new Rect();

	public ScrollView1(Context context) {
		super(context);
	}

	public ScrollView1(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollView1(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		if (getChildCount() > 0) {
			inner = getChildAt(0);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (inner == null) {
			return super.onTouchEvent(ev);
		} else {
			commOnTouchEvent(ev);
		}
		return super.onTouchEvent(ev);
	}


	public void commOnTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			y = ev.getY();
			break;
		case MotionEvent.ACTION_UP:
			if (isNeedAnimation()) {
				// Log.v("mlguitar", "will up and animation");
				animation();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			final float preY = y;
			float nowY = ev.getY();
			/**
			 * size=4 ��ʾ �϶��ľ���Ϊ��Ļ�ĸ߶ȵ�1/4
			 */
			int deltaY = (int) (preY - nowY) / size;
			// ����
			// scrollBy(0, deltaY);

			y = nowY;
			// �����������ϻ�������ʱ�Ͳ����ٹ�������ʱ�ƶ�����
			if (isNeedMove()) {
				if (normal.isEmpty()) {
					// ���������Ĳ���λ��
					normal.set(inner.getLeft(), inner.getTop(),
							inner.getRight(), inner.getBottom());
					return;
				}
				int yy = inner.getTop() - deltaY;

				// �ƶ�����
				inner.layout(inner.getLeft(), yy, inner.getRight(),
						inner.getBottom() - deltaY);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();

			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;

			if (xDistance > yDistance) {
				return false;
			}

		}

		return super.onInterceptTouchEvent(ev);
	}

	// ���������ƶ�

	public void animation() {
		// �����ƶ�����
		TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(),
				normal.top);
		ta.setDuration(200);
		inner.startAnimation(ta);
		// ���ûص������Ĳ���λ��
		inner.layout(normal.left, normal.top, normal.right, normal.bottom);
		normal.setEmpty();
	}

	// �Ƿ���Ҫ��������
	public boolean isNeedAnimation() {
		return !normal.isEmpty();
	}

	// �Ƿ���Ҫ�ƶ�����
	public boolean isNeedMove() {
		int offset = inner.getMeasuredHeight() - getHeight();
		int scrollY = getScrollY();
		if (scrollY == 0 || scrollY == offset) {
			return true;
		}
		return false;
	}
}
