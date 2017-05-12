package com.sampah_ku.sampahku.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sampah_ku.sampahku.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RewardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RewardFragment extends Fragment {

    public RewardFragment() {
        // Required empty public constructor
    }

    public static RewardFragment newInstance() {
        RewardFragment fragment = new RewardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment\
        View rootView = inflater.inflate(R.layout.fragment_reward, container, false);
        String[] values = new String[]{"Mengupload Story", "Menambah Tempat Sampah", "Melaporkan Kesalahan Tempat Sampah", "Mengupload Story", "Menambah Tempat Sampah", "Melaporkan Kesalahan Tempat Sampah", "Mengupload Story", "Menambah Tempat Sampah", "Melaporkan Kesalahan Tempat Sampah"};
        ListView listView = (ListView) rootView.findViewById(R.id.listkategori);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_view_content, R.id.itemJudul, values);
        listView.setAdapter(adapter);

        return rootView;
    }

}
