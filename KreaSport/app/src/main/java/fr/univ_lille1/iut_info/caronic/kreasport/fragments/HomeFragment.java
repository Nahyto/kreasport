package fr.univ_lille1.iut_info.caronic.kreasport.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.univ_lille1.iut_info.caronic.kreasport.MainActivity;
import fr.univ_lille1.iut_info.caronic.kreasport.R;

public class HomeFragment extends Fragment {

    private static final String LOG = HomeFragment.class.getSimpleName();

    public static final String DOWNLOAD_REQUEST = "kreasport.fragment_home.request.reason";

    public static final String DOWNLOAD_PRIVATE_RACE = "kreasport.frag_request_code.download_private_race";
    public static final String DOWNLOAD_PRIVATE_RACE_KEY = "kreasport.frag_request_code.download_private_race_key";


    @BindView(R.id.fragment_home_et_key)
    EditText etKey;

    @OnClick(R.id.fragment_home_tv_download_public_races)
    public void downloadPublicRaces() {
        downloadRace(false);
    }

    @OnClick(R.id.fragment_home_button_download_from_key)
    public void downloadPrivateRace() {
        downloadRace(true);
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void downloadRace(boolean privateRace) {
        if (mListener != null) {
            Intent intent = new Intent();
            intent.putExtra(MainActivity.CALLBACK_KEY, DOWNLOAD_REQUEST);

            if (privateRace) {
                intent.putExtra(DOWNLOAD_PRIVATE_RACE, true);
                intent.putExtra(DOWNLOAD_PRIVATE_RACE_KEY, etKey.getText().toString());
            } else {
                intent.putExtra(DOWNLOAD_PRIVATE_RACE, false);
            }

            mListener.onFragmentInteraction(intent);
        } else {
            Log.d(LOG, "could not send download request as mListener was null");
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
