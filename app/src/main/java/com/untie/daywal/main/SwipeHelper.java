package com.untie.daywal.main;

import 	androidx.recyclerview.widget.RecyclerView;
import  androidx.recyclerview.widget.ItemTouchHelper;

import com.untie.daywal.activity.PopupAdapter;

/**
 * Created by ichaeeun on 2016. 11. 27..
 */

public class SwipeHelper extends ItemTouchHelper.SimpleCallback {
    PopupAdapter adapter;

    public SwipeHelper(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    public SwipeHelper(PopupAdapter adapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }



    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.dismissItem(viewHolder.getAdapterPosition());
    }
}