package com.memeit.backend;

import com.memeit.backend.dataclasses.Badge;
import com.memeit.backend.dataclasses.Notification;
import com.memeit.backend.dataclasses.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.memeit.backend.MemeItAuth.PREFERENCE_USER_DATA_SAVED;
import static com.memeit.backend.OnCompleteListener.Error.OTHER_ERROR;

public class MemeItUsers {
    private static MemeItUsers memeItUsers;

    private MemeItUsers() {

    }
    public static void init(){
        memeItUsers=new MemeItUsers();
    }
    public static MemeItUsers getInstance(){
        return memeItUsers;
    }

    /**
     * this is to get the user detail
     *@param listener the Listener to be called when the action is completed
     **/
    public void getMyUyserDetail(OnCompleteListener<User> listener){

    }
    /**
     * this is to get user detail for a specified id
     * @param uid the id of the user to get detail for
     *@param listener the Listener to be called when the action is completed
     **/
    public void getUserDetailFor(String uid,OnCompleteListener<User> listener){

    }

    /**
     * this is to get the notifications of the user
     *@param skip to skip some notifications from the retrieved list
     *@param limit to limit the number of retrieved notifications
     *@param listener the Listener to be called when the action is completed
     **/
    public void getNotificationList(int skip,int limit,OnCompleteListener<List<Notification>> listener){

    }

    /**
     * this is to get the number of unseen notification of the user
     *@param listener the Listener to be called when the action is completed
     **/
    public void getNotificationCount(OnCompleteListener<Integer> listener){

    }
    /**
     * this is to get the followers list of the user
     *@param skip to skip some followers from the retrieved list
     *@param limit to limit the number of retrieved followers list
     *@param listener the Listener to be called when the action is completed
     **/
    public void getMyFollowerList(int skip,int limit,OnCompleteListener<List<User>> listener){

    }
    /**
     * this is to get the list of users the user follow
     *@param skip skips the specified amount in the list
     *@param limit limits the number of items in the list
     *@param listener the Listener to be called when the action is completed
     **/
    public void getMyFollowingList(int skip,int limit,OnCompleteListener<List<User>> listener){

    }

    /**
     * this is to get the list of badges the user gained
     *@param listener the Listener to be called when the action is completed
     **/
    public void getMyBadges(OnCompleteListener<List<Badge>> listener){

    }

    /**
     * this is to get the followers list of a specified user
     *@param uid the user id to get the list for
     *@param skip skips the specified amount in the list
     *@param limit limits the number of items in the list
     *@param listener the Listener to be called when the action is completed
     **/
    public void getFollowerListFor(String uid,int skip,int limit,OnCompleteListener<List<User>> listener){

    }
    /**
     * this is to get the list of users a specified user follow
     *@param uid the user id to get the list for
     *@param skip skips the specified amount in the list
     *@param limit limits the number of items in the list
     *@param listener the Listener to be called when the action is completed
     **/
    public void getFollowingListFor(String uid,int skip,int limit,OnCompleteListener<List<User>> listener){

    }

    /**
     * this is to get the list of badges a specified user gained
     *@param uid the user id to get the list for
     *@param listener the Listener to be called when the action is completed
     **/
    public void getBadgesFor(String uid,OnCompleteListener<List<Badge>> listener){

    }


    //====================================================================================================================

    /**
     * this is to follow a user
     * @param uid  the user id of ther user to follow
     *@param listener the Listener to be called when the action is completed
     * **/
    public void followUser(String uid,OnCompleteListener<Void> listener){

    }
    /**
     * this is to unfollow a user
     *@param uid  the user id of ther user to follow
     *@param listener the Listener to be called when the action is completed
     * **/
    public void unFollowUser(String uid,OnCompleteListener<Void> listener){

    }

    /**
     * this is to update the user data
     * i.e to update the user's name,username or profile picture
     *@param user  the updated user data
     *@param listener the Listener to be called when the action is completed
     * **/
    public void updateMyData(User user,OnCompleteListener<Void> listener){
        MemeItClient.getInstance().getInterface()
                .uploadUserData(user)
                .enqueue(new MyCallBack<User, Void>(listener) {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            User user = response.body();
                            PrefUtils.get().edit()
                                    .putBoolean(PREFERENCE_USER_DATA_SAVED, true)
                                    .apply();
                        } else {
                            Utils.checkAndFireError(listener, OTHER_ERROR.setMessage(response.message()));
                        }
                    }
                });
    }

    /**
     *this is to mark a specific notification seen
     *@param nid the notification id to mark seen
     *@param listener the Listener to be called when the action is completed
     * **/
    public void markNotificationSeen(String nid,OnCompleteListener<Void> listener){

    }
    /**
     * this is to mark all notification seen
     *@param listener the Listener to be called when the action is completed
    **/
    public void markAllNotificationSeen(OnCompleteListener<Void> listener){

    }

    /**
     * This is to delete the user account completely
     * Care must be taken here
     * @param listener the Listener to be called when the action is completed
     */
    public void deleteMe(OnCompleteListener<Void> listener){

    }




}
