package android.support.design.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;

/**
 * @author Eugen on 25.02.2017.
 */

public class IconifiedTabLayout extends TabLayout {
    private PagerAdapter mPagerAdapter = null;

    public IconifiedTabLayout(final Context context) {
        super(context);
    }

    public IconifiedTabLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public IconifiedTabLayout(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPagerAdapter(final PagerAdapter pagerAdapter, final boolean addObserver) {
        mPagerAdapter = pagerAdapter;
        super.setPagerAdapter(pagerAdapter, addObserver);
    }

    @Override
    void populateFromPagerAdapter() {
        removeAllTabs();

        if (mPagerAdapter != null) {
            final IconifiedPagerAdapter iconifiedAdapter = getIconifiedPagerAdapter(mPagerAdapter);

            final int adapterCount = mPagerAdapter.getCount();
            for (int i = 0; i < adapterCount; i++) {
                final Tab tab = newTab();
                final CharSequence pageTitle = mPagerAdapter.getPageTitle(i);
                if (iconifiedAdapter.getDisplayPageTitle(i)) {
                    tab.setText(pageTitle);
                } else {
                    tab.setContentDescription(pageTitle);
                }
                tab.setIcon(iconifiedAdapter.getPageIcon(i));
                addTab(tab, false);
            }

            // Make sure we reflect the currently set ViewPager item
            if (mViewPager != null && adapterCount > 0) {
                final int curItem = mViewPager.getCurrentItem();
                if (curItem != getSelectedTabPosition() && curItem < getTabCount()) {
                    selectTab(getTabAt(curItem));
                }
            }
        }
    }

    private IconifiedPagerAdapter getIconifiedPagerAdapter(final PagerAdapter adapter) {
        if (adapter instanceof IconifiedPagerAdapter) {
            return (IconifiedPagerAdapter) adapter;
        } else {
            return IconifiedPagerAdapter.NOOP;
        }
    }
}
