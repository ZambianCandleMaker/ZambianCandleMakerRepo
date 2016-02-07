package edu.rose_hulman.trottasn.zambiancandlemakerinterface;

/**
 * Created by TrottaSN on 2/6/2016.
 */
public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}