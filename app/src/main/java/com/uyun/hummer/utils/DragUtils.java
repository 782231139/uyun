/*
 * Copyright (c) 2014-2015 Zhang Hai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uyun.hummer.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Service;
import android.os.Vibrator;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.WebView;

// TODO: Provide scrolling at edge, see
// https://github.com/justasm/DragLinearLayout/blob/master/library/src/main/java/com/jmedeisis/draglinearlayout/DragLinearLayout.java
// and
// https://github.com/nhaarman/ListViewAnimations/blob/master/lib-manipulation/src/main/java/com/nhaarman/listviewanimations/itemmanipulation/dragdrop/DragAndDropHandler.java
public class DragUtils{
    private static int pos_x;
    private static int pos_y;
    private DragUtils() {}

    public static void setupDragSort(final View view, final DragListener listener) {
        view.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(final View view, DragEvent event) {
                ViewGroup viewGroup = (ViewGroup)view.getParent();
                DragState dragState = (DragState)event.getLocalState();
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        if (view == dragState.view) {
                            view.setVisibility(View.INVISIBLE);
                            listener.onDragStarted(view);
                        }
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION: {
                        if (view == dragState.view){
                            break;
                        }
                        int index = viewGroup.indexOfChild(view);
                        if ((index > dragState.index && event.getY() > view.getHeight() / 2)
                                || (index < dragState.index && event.getY() < view.getHeight() / 2)) {
                            swapViews(viewGroup, view, index, dragState);
                        } else {
                            swapViewsBetweenIfNeeded(viewGroup, index, dragState);
                        }
                        break;
                    }
                    case DragEvent.ACTION_DRAG_ENDED:
                        if (view == dragState.view) {
                            view.setVisibility(View.VISIBLE);
                            Log.i("yunli","ACTION_DRAG_ENDED");
                            listener.onDragEnded(view);
                        }
                        break;
                }
                return true;
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Vibrator vib = (Vibrator) view.getContext().getSystemService(Service.VIBRATOR_SERVICE);//震动70毫秒
                vib.vibrate(70);
                view.startDrag(null, new MyDragShadowBuilder(view,pos_x,pos_y), new DragState(view), 0);
                return true;
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    pos_x = (int)event.getX();
                    pos_y = (int)event.getY();
                }
                return false;
            }
        });
    }

    public static interface DragListener {
        public void onDragStarted(View view);
        public void onDragEnded(View view);
    }
    private static void swapViewsBetweenIfNeeded(ViewGroup viewGroup, int index,
                                                 DragState dragState) {
        if (index - dragState.index > 1) {
            int indexAbove = index - 1;
            swapViews(viewGroup, viewGroup.getChildAt(indexAbove), indexAbove, dragState);
        } else if (dragState.index - index > 1) {
            int indexBelow = index + 1;
            swapViews(viewGroup, viewGroup.getChildAt(indexBelow), indexBelow, dragState);
        }
    }

    private static void swapViews(ViewGroup viewGroup, final View view, int index,
                                  DragState dragState) {
        swapViewsBetweenIfNeeded(viewGroup, index, dragState);
        final float viewY = view.getY();
        AppUtils.swapViewGroupChildren(viewGroup, view, dragState.view);
        dragState.index = index;
        AppUtils.postOnPreDraw(view, new Runnable() {
            @Override
            public void run() {
                ObjectAnimator
                        .ofFloat(view, View.Y, viewY, view.getTop())
                        .setDuration(getDuration(view))
                        .start();
            }
        });
    }

    private static int getDuration(View view) {
        return view.getResources().getInteger(android.R.integer.config_shortAnimTime);
    }


    private static class DragState {

        public View view;
        public int index;

        private DragState(View view) {
            this.view = view;
            index = ((ViewGroup)view.getParent()).indexOfChild(view);
        }
    }
}
