/*
 *    Copyright (C) 2015
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.advrecyclerviewswipe.sample;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<ConcreteData> mData;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    private void init() {
        // generate dummy data for recyclerview
        mData = generateDummyData();

        //noinspection ConstantConditions
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);

        // touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
        mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
        mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
        mRecyclerViewTouchActionGuardManager.setEnabled(true);

        // swipe manager
        mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

        //adapter
        final SwipeableWithButtonExampleAdapter myItemAdapter = new SwipeableWithButtonExampleAdapter(mData);
        myItemAdapter.setEventListener(new SwipeableWithButtonExampleAdapter.EventListener() {
            @Override
            public void onItemPinned(int position) {
                handleOnItemPinned(position);
            }

            @Override
            public void onItemViewClicked(View v) {
                handleOnItemViewClicked(v);
            }

            @Override
            public void onUnderSwipeableViewButtonClicked(View v) {
                handleOnUnderSwipeableViewButtonClicked(v);
            }
        });

        mAdapter = myItemAdapter;

        mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(myItemAdapter);      // wrap for swiping

        final GeneralItemAnimator animator = new SwipeDismissItemAnimator();

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Disable the change animation in order to make turning back animation of swiped item works properly.
        animator.setSupportsChangeAnimations(false);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            mRecyclerView.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) ContextCompat.getDrawable(this, R.drawable.material_shadow_z1)));
        }
        mRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(this, R.drawable.list_divider_h), true));

        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop
        mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
        mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);

        // for debugging
//        animator.setDebug(true);
//        animator.setMoveDuration(2000);
//        mRecyclerViewSwipeManager.setMoveToOutsideWindowAnimationDuration(2000);
//        mRecyclerViewSwipeManager.setReturnToDefaultPositionAnimationDuration(2000);
    }

    private void handleOnItemPinned(int position) {
        //TODO do something when the item gets pinned
    }

    private void handleOnUnderSwipeableViewButtonClicked(View v) {
        int position = mRecyclerView.getChildAdapterPosition(v);
        String text = getString(R.string.snack_bar_text_button_clicked, position);

        Snackbar snackbar = Snackbar.make(
                findViewById(R.id.container),
                text,
                Snackbar.LENGTH_SHORT);

        snackbar.show();
    }

    private void handleOnItemViewClicked(View v) {
        int position = mRecyclerView.getChildAdapterPosition(v);
        ConcreteData data = mData.get(position);
        if (data.isPinned()) {
            // unpin if tapped the pinned item
            data.setPinned(false);
            mAdapter.notifyItemChanged(position);
        }
    }

    private List<ConcreteData> generateDummyData() {
        final String atoz = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        List<ConcreteData> mData = new LinkedList<>();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < atoz.length(); j++) {
                final long id = mData.size();
                final int viewType = 0;
                final String text = Character.toString(atoz.charAt(j));
                final int swipeReaction = RecyclerViewSwipeManager.REACTION_CAN_SWIPE_UP | RecyclerViewSwipeManager.REACTION_CAN_SWIPE_DOWN;
                mData.add(new ConcreteData(id, viewType, text, swipeReaction));
            }
        }
        return mData;
    }


    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }
}
