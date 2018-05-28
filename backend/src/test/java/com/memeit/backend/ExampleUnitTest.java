package com.memeit.backend;

import android.util.Log;

import com.memeit.backend.dataclasses.User;
import com.memeit.backend.utilis.OnCompleteListener;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private static final String SERVER_URL="http://127.0.0.1:5000/api/";
    private static String TAG="fuck";
    @BeforeClass
    public static  void init(){
        MemeItClient.init(null,SERVER_URL);
    }
    
    @Test(timeout =10)    
    public void addition_isCorrect() {
        assertEquals(5,6);
        MemeItUsers.getInstance().getMyFollowerList(0, 3, new OnCompleteListener<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                assertEquals(5,6);
                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onFailure(Error error) {
                assertEquals("failed",5,6);
                Log.d(TAG, "onFailure: ");
            }
        });
        
    }
}