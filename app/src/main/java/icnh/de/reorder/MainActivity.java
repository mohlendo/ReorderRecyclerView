/*
 * Copyright (C) 2014 I.C.N.H GmbH
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
package icnh.de.reorder;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mohlendo.com.reorder.R;

public class MainActivity extends ActionBarActivity {
    private List<String> cheeseList;

    private RecyclerView recyclerView;
    private int selectedLayoutMangerIndex = 2;
    private List<RecyclerView.LayoutManager> availableLayoutManager = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        cheeseList = new ArrayList<>();
        Collections.addAll(cheeseList, Cheeses.sCheeseStrings);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.addItemDecoration(new InsetDecoration(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        availableLayoutManager.add(new LinearLayoutManager(this));
        availableLayoutManager.add(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        availableLayoutManager.add(new GridLayoutManager(this, 2));
        availableLayoutManager.add(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));

        if (savedInstanceState != null) {
            selectedLayoutMangerIndex = savedInstanceState.getInt("selectedLayoutMangerIndex");
        }

        recyclerView.setLayoutManager(availableLayoutManager.get(selectedLayoutMangerIndex));


        ReorderRecyclerView.ReorderAdapter adapter = new ReorderRecyclerView.ReorderAdapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.text_view, parent, false);
                return new CheeseViewHolder(inflate);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((CheeseViewHolder) holder).textView.setText(cheeseList.get(position));
            }

            @Override
            public int getItemCount() {
                return cheeseList.size();
            }

            @Override
            public long getItemId(int position) {
                return cheeseList.get(position).hashCode();
            }

            @Override
            public void swapElements(int fromIndex, int toIndex) {
                String temp = cheeseList.get(fromIndex);
                cheeseList.set(fromIndex, cheeseList.get(toIndex));
                cheeseList.set(toIndex, temp);
            }
        };
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedLayoutMangerIndex", selectedLayoutMangerIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        selectedLayoutMangerIndex = 0;
        int id = item.getItemId();
        switch (id) {
            case R.id.action_linear_vertical:
                selectedLayoutMangerIndex = 0;
                break;
            case R.id.action_linear_horizontal:
                selectedLayoutMangerIndex = 1;
                break;
            case R.id.action_vertical_grid:
                selectedLayoutMangerIndex = 2;
                break;
            case R.id.action_horizontal_grid:
                selectedLayoutMangerIndex = 3;
                break;
            default:
                break;
        }

        recyclerView.setLayoutManager(availableLayoutManager.get(selectedLayoutMangerIndex));

        return super.onOptionsItemSelected(item);
    }

    private static final class CheeseViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public CheeseViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(textView.getContext(), textView.getText(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private static final class InsetDecoration extends RecyclerView.ItemDecoration {

        private int mInsets;

        public InsetDecoration(Context context) {
            mInsets = context.getResources().getDimensionPixelSize(R.dimen.card_insets);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //We can supply forced insets for each item view here in the Rect
            outRect.set(mInsets, mInsets, mInsets, mInsets);
        }
    }
}
