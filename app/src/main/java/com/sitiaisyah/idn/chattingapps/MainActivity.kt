package com.sitiaisyah.idn.chattingapps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.sitiaisyah.idn.chattingapps.fragment.ChatFragment
import com.sitiaisyah.idn.chattingapps.fragment.SearchFragment
import com.sitiaisyah.idn.chattingapps.fragment.SettingFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(tool_bar)

        val toolBar: Toolbar = findViewById(R.id.tool_bar)
        setActionBar(toolBar)
        supportActionBar!!.title = ""

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager: ViewPager = findViewById(R.id.view_pager)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(ChatFragment(), "chats")
        viewPagerAdapter.addFragment(SearchFragment(), "search")
        viewPagerAdapter.addFragment(SettingFragment(), "setting")

        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager) {

        private val titles : ArrayList<String>
        private val fragments : ArrayList<Fragment>
        init{
            titles = ArrayList()
            fragments = ArrayList()
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }

        fun addFragment(fragment : Fragment, title : String) {
            fragments.add(fragment)
            titles.add(title)
        }
    }

}
