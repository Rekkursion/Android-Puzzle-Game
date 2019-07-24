package com.rekkursion.puzzlegame;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewOnItemTouchListener extends RecyclerView.SimpleOnItemTouchListener {
    private GestureDetectorCompat gestureDetector;
    private OnItemClickListener onItemClickListener;

    // interface define
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    // constructor
    public RecyclerViewOnItemTouchListener(final RecyclerView recv, final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        gestureDetector = new GestureDetectorCompat(recv.getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        View childView = recv.findChildViewUnder(e.getX(), e.getY());
                        if (childView != null && onItemClickListener != null)
                            onItemClickListener.onItemClick(childView, recv.getChildAdapterPosition(childView));
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        View childView = recv.findChildViewUnder(e.getX(), e.getY());
                        if (childView != null && onItemClickListener != null)
                            onItemClickListener.onItemLongClick(childView, recv.getChildAdapterPosition(childView));
                    }
                });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }
}
