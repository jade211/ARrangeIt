package com.example.arrangeit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedDispatcher;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.arrangeit.helpers.FurnitureItem;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FurnitureDetailUnitTest {

    @Mock
    private Context mockContext;
    @Mock
    private LayoutInflater mockInflater;
    @Mock
    private ViewGroup mockContainer;
    @Mock
    private Bundle mockBundle;
    @Mock
    private View mockView;
    @Mock
    private TextView mockItemName;
    @Mock
    private TextView mockItemDescription;
    @Mock
    private TextView mockItemPrice;
    @Mock
    private TextView mockItemHeight;
    @Mock
    private TextView mockItemWidth;
    @Mock
    private TextView mockItemDepth;
    @Mock
    private TextView mockItemColours;
    @Mock
    private TextView mockItemTexture;
    @Mock
    private ImageView mockItemImage;
    @Mock
    private Button mockPlaceInArButton;
    @Mock
    private View mockBackButton;
    @Mock
    private FragmentActivity mockActivity;
    @Mock
    private FragmentManager mockFragmentManager;
    @Mock
    private FragmentTransaction mockTransaction;
    @Mock
    private FirebaseStorage mockFirebaseStorage;
    @Mock
    private StorageReference mockStorageReference;
    @Mock
    private Task<android.net.Uri> mockUriTask;
    @Mock
    private OnBackPressedDispatcher mockOnBackPressedDispatcher;

    private FurnitureDetailFragment fragment;
    private FurnitureItem testFurnitureItem;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fragment = spy(new FurnitureDetailFragment());
        testFurnitureItem = new FurnitureItem(
                "Test Chair",
                "Chair",
                "Test Description",
                129.99,
                "Black",
                "images/test_chair.png",
                "models/test_chair.glb",
                "Leather",
                90.0,
                60.0,
                55.0
        );

        when(mockView.findViewById(R.id.itemName)).thenReturn(mockItemName);
        when(mockView.findViewById(R.id.itemDescription)).thenReturn(mockItemDescription);
        when(mockView.findViewById(R.id.itemPrice)).thenReturn(mockItemPrice);
        when(mockView.findViewById(R.id.itemHeight)).thenReturn(mockItemHeight);
        when(mockView.findViewById(R.id.itemWidth)).thenReturn(mockItemWidth);
        when(mockView.findViewById(R.id.itemDepth)).thenReturn(mockItemDepth);
        when(mockView.findViewById(R.id.itemColours)).thenReturn(mockItemColours);
        when(mockView.findViewById(R.id.itemTexture)).thenReturn(mockItemTexture);
        when(mockView.findViewById(R.id.itemImage)).thenReturn(mockItemImage);
        when(mockView.findViewById(R.id.place_in_ar_button)).thenReturn(mockPlaceInArButton);
        when(mockView.findViewById(R.id.back_button)).thenReturn(mockBackButton);

        // mock inflater
        when(mockInflater.inflate(R.layout.fragment_furniture_detail, mockContainer, false)).thenReturn(mockView);
        when(mockBundle.getSerializable("furniture_item")).thenReturn(testFurnitureItem);

        // Mock activity and fragment manager
        when(mockActivity.getSupportFragmentManager()).thenReturn(mockFragmentManager);
        when(mockFragmentManager.beginTransaction()).thenReturn(mockTransaction);
        when(mockTransaction.replace(any(Integer.class), any(Fragment.class))).thenReturn(mockTransaction);
        when(mockTransaction.remove(any(Fragment.class))).thenReturn(mockTransaction);
        when(mockActivity.getOnBackPressedDispatcher()).thenReturn(mockOnBackPressedDispatcher);

        // mock firebase
        mockFirebaseStorage = mock(FirebaseStorage.class);
        mockStorageReference = mock(StorageReference.class);
        mockUriTask = mock(Task.class);

        doReturn(mockFirebaseStorage).when(fragment).getFirebaseStorage();
        when(mockFirebaseStorage.getReference(anyString())).thenReturn(mockStorageReference);
        when(mockStorageReference.getDownloadUrl()).thenReturn(mockUriTask);
        when(mockUriTask.addOnSuccessListener(any())).thenReturn(mockUriTask);
        when(mockUriTask.addOnFailureListener(any())).thenReturn(mockUriTask);
    }

    @Test
    public void testDisplayFurnitureDetails() {
        fragment.setArguments(mockBundle);

        // ***** Set mock Activity and context
        doReturn(mockActivity).when(fragment).getActivity();
        doReturn(mockActivity).when(fragment).requireActivity();
        doReturn(mockContext).when(fragment).requireContext();
        // mock getViewLifecycleOwner to prevent lifecycle-related crashes
        doReturn(mock(Fragment.class)).when(fragment).getViewLifecycleOwner();

        fragment.itemName = mockItemName;
        fragment.itemDescription = mockItemDescription;
        fragment.itemPrice = mockItemPrice;
        fragment.itemHeight = mockItemHeight;
        fragment.itemWidth = mockItemWidth;
        fragment.itemDepth = mockItemDepth;
        fragment.itemColours = mockItemColours;
        fragment.itemTexture = mockItemTexture;
        fragment.itemImage = mockItemImage;

        View resultView = fragment.onCreateView(mockInflater, mockContainer, mockBundle);

        verify(mockItemName).setText("Test Chair");
        verify(mockItemDescription).setText("Test Description");
        verify(mockItemPrice).setText("€129.99");
        verify(mockItemHeight).setText("Height: 90.0cm");
        verify(mockItemWidth).setText("Width: 60.0cm");
        verify(mockItemDepth).setText("Depth: 55.0cm");
        verify(mockItemColours).setText("Black");
        verify(mockItemTexture).setText("Leather");
    }


    @Test
    public void testImageLoading() {
        when(fragment.getContext()).thenReturn(mockContext);
        when(fragment.requireContext()).thenReturn(mockContext);
        doReturn(mockActivity).when(fragment).getActivity();
        doReturn(mockActivity).when(fragment).requireActivity();
        doReturn(mock(Fragment.class)).when(fragment).getViewLifecycleOwner();

        fragment.itemImage = mockItemImage;
        fragment.setArguments(mockBundle);
        View resultView = fragment.onCreateView(mockInflater, mockContainer, mockBundle);

        verify(mockStorageReference).getDownloadUrl();
    }

    @Test
    public void testDimensionsDisplay() {
        doReturn(mockActivity).when(fragment).getActivity();
        doReturn(mockActivity).when(fragment).requireActivity();
        doReturn(mock(Fragment.class)).when(fragment).getViewLifecycleOwner();
        fragment.setArguments(mockBundle);

        fragment.itemHeight = mockItemHeight;
        fragment.itemWidth = mockItemWidth;
        fragment.itemDepth = mockItemDepth;
        fragment.furnitureItem = testFurnitureItem;

        View resultView = fragment.onCreateView(mockInflater, mockContainer, mockBundle);

        verify(mockItemHeight).setText("Height: 90.0cm");
        verify(mockItemWidth).setText("Width: 60.0cm");
        verify(mockItemDepth).setText("Depth: 55.0cm");
    }
}
