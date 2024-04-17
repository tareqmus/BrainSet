import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.brainset.ocr.R;


public class SettingsFragment extends Fragment {
    View view;
    Activity a;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_scan_list, container, false);

        view = view.findViewById(R.id.setting);
        a = this.getActivity();
    }
}