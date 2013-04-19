package com.discover.mobile.card.common.ui.modals;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.LinearLayout;

import com.discover.mobile.card.R;

/**
 * This class is a modal alert with both a top view and a bottom view. This view
 * will only show a title and some content text and a bottom with two buttons.
 * 
 * @author jthornton
 * 
 */
public class ModalAlertWithTwoButtons extends AlertDialog
{

    /** Orientation listener */
    private OrientationEventListener orientationListener;

    /** Top view too be displayed */
    private final ModalTopView top;

    /** Bottom view to be displayed */
    private final ModalBottomTwoButtonView bottom;

    /** Application Context */
    private final Context context;

    /** Linear layout that holds the top and bottom views */
    private LinearLayout linearLayout;

    /** Static int for the heights of the top and bottom views */
    private static final int VIEW_HEIGHTS = 0;

    /** Static weight for the top view in portrait mode */
    private static final float PORTRAIT_TOP_WEIGHT = 7f;

    /** Static weight for the bottom view in portrait mode */
    private static final float PORTRAIT_BOTTOM_WEIGHT = 3f;

    /** Static weight for the top view in landscape mode */
    private static final float LANDSCAPE_TOP_WEIGHT = 4f;

    /** Static weight for the bottom view in landscape mode */
    private static final float LANDSCAPE_BOTTOM_WEIGHT = 6f;

    /** Static variable for the orientation */
    static int orientation = 0;

    /**
     * Constructor for the alert
     * 
     * @param context
     *            - activity context
     * @param top
     *            - top piece to be displayed
     * @param bottom
     *            - bottom piece to be displayed
     */
    public ModalAlertWithTwoButtons(final Context context, final ModalTopView top, final ModalBottomTwoButtonView bottom)
    {

        super(context);
        this.context = context;
        this.top = top;
        this.bottom = bottom;
    }

    /**
     * An alternate way to create a modal alert with one button by supplying
     * content only
     * 
     * @param context
     *            - application context
     * @param title
     *            - the title for the alert
     * @param content
     *            - the body text for the alert
     * @param showErrorIcon
     *            - used to show the error icon on the top view
     * @param helpNumber
     *            - help number to be displayed in the modal
     * @param buttonText
     *            - the button text for the alert
     */
    public ModalAlertWithTwoButtons(final Context context, final String title, final String content, final boolean showErrorIcon, final int helpNumber, final int okBtnText, final int cancelBtnText)
    {

        super(context);

        this.context = context;
        final ModalDefaultTopView topView = new ModalDefaultTopView(context, null);
        final ModalDefaultTwoButtonBottomView bottomView = new ModalDefaultTwoButtonBottomView(context, null);

        topView.showErrorIcon(showErrorIcon);
        topView.setTitle(title);
        topView.setContent(content);
        topView.getHelpFooter().setToDialNumberOnClick(helpNumber);
        bottomView.setOkButtonText(okBtnText);
        bottomView.setCancelButtonText(cancelBtnText);

        top = topView;
        bottom = bottomView;

    }

    /**
     * Create the modal alert and add the views to be displayed.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final View mainView = this.getLayoutInflater().inflate(R.layout.modal_alert_layout, null);
        this.setContentView(mainView);
        linearLayout = (LinearLayout) mainView.findViewById(R.id.modal_linear_layout);

        orientationListener = this.createOrientationListener();
    }

    /**
     * Start the modal correctly
     */
    @Override
    public void onStart()
    {
        display();
    }

    /**
     * Get the top piece so that it can be manipulated
     * 
     * @return the top piece so that it can be manipulated
     */
    public ModalTopView getTop()
    {
        return top;
    }

    /**
     * Get the bottom piece so that it can be manipulated
     * 
     * @return the bottom piece so that it can be manipulated
     */
    public ModalBottomTwoButtonView getBottom()
    {
        return bottom;
    }

    /**
     * Get the orientation listener instance
     * 
     * @return orientation listener instance
     */
    public OrientationEventListener getOrientationEventListener()
    {
        return orientationListener;
    }

    /**
     * Create the orientation changed listener
     * 
     * @return the orientation changed listener
     */
    public OrientationEventListener createOrientationListener()
    {
        final OrientationEventListener ret = new OrientationEventListener(this.getContext(), SensorManager.SENSOR_DELAY_NORMAL)
        {
            @Override
            public void onOrientationChanged(final int arg0)
            {
                if (orientation != context.getResources().getConfiguration().orientation)
                {
                    orientation = context.getResources().getConfiguration().orientation;
                    display();
                }
            }
        };

        ret.enable();
        return ret;
    }

    /**
     * Display the layout with the correct layout.
     */
    public void display()
    {
        final int orientation = context.getResources().getConfiguration().orientation;
        float topWeight;
        float bottomWeight;
        if (Configuration.ORIENTATION_LANDSCAPE == orientation)
        {
            topWeight = LANDSCAPE_TOP_WEIGHT;
            bottomWeight = LANDSCAPE_BOTTOM_WEIGHT;
        }
        else
        {
            topWeight = PORTRAIT_TOP_WEIGHT;
            bottomWeight = PORTRAIT_BOTTOM_WEIGHT;
        }

        final LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, VIEW_HEIGHTS, topWeight);

        final LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, VIEW_HEIGHTS, bottomWeight);

        linearLayout.removeAllViews();
        if (null != top)
        {
            linearLayout.addView((View) top, p1);
        }
        if (null != bottom)
        {
            linearLayout.addView((View) bottom, p2);
        }
    }
}
