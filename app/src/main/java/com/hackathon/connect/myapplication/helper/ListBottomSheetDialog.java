package com.hackathon.connect.myapplication.helper;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hackathon.connect.myapplication.R;


/**
 * Created by vivek on 9/9/16.
 */
public class ListBottomSheetDialog extends BottomSheetDialogFragment {

    public interface IListBottomSheet
    {
        String getTitle();
        View getContentView();
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCallBack;
    private IListBottomSheet mIListBottomSheet;

    public static ListBottomSheetDialog newInstance(BottomSheetBehavior.BottomSheetCallback bottomSheetDialogCallback, IListBottomSheet iListBottomSheet) {

        Bundle args = new Bundle();
        ListBottomSheetDialog fragment = new ListBottomSheetDialog();
        fragment.setArguments(args);
        fragment.setBottomSheetCallBack(bottomSheetDialogCallback);
        fragment.setListBottomSheet(iListBottomSheet);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void setBottomSheetCallBack(BottomSheetBehavior.BottomSheetCallback mBottomSheetCallBack) {
        this.mBottomSheetCallBack = mBottomSheetCallBack;
    }

    public void setListBottomSheet(IListBottomSheet iListBottomSheet) {
        this.mIListBottomSheet = iListBottomSheet;
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.layout_bottom_sheet, null);
        LinearLayout lin_bottom_sheet_wrapper = (LinearLayout)contentView.findViewById(R.id.lin_bottom_sheet_wrapper);
        lin_bottom_sheet_wrapper.removeAllViews();
        if(mIListBottomSheet!=null)
            lin_bottom_sheet_wrapper.addView(mIListBottomSheet.getContentView());

       /* TextView txt_title = (TextView)contentView.findViewById(R.id.txt_title);
        if(mIListBottomSheet!=null) {
            if(mIListBottomSheet.getTitle() !=null && !mIListBottomSheet.getTitle().equals(""))
                txt_title.setText(mIListBottomSheet.getTitle());
            else
                txt_title.setVisibility(View.GONE);
        }*/
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior) .setState(BottomSheetBehavior.STATE_EXPANDED);
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetCallBack);
        }
    }



}
