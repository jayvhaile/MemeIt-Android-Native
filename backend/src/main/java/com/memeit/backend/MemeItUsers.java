package com.memeit.backend;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.memeit.backend.dataclasses.AuthInfo;
import com.memeit.backend.dataclasses.Badge;
import com.memeit.backend.dataclasses.MyUser;
import com.memeit.backend.dataclasses.Tag;
import com.memeit.backend.dataclasses.User;
import com.memeit.backend.dataclasses.Username;
import com.memeit.backend.utilis.MyCallBack;
import com.memeit.backend.utilis.MyCallBack2;
import com.memeit.backend.utilis.OnCompleteListener;
import com.memeit.backend.utilis.PrefUtils;
import com.memeit.backend.utilis.Utils;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.memeit.backend.MemeItAuth.PREFERENCE_USER_DATA_SAVED;
import static com.memeit.backend.utilis.OnCompleteListener.Error.OTHER_ERROR;

public class MemeItUsers {
    private static MemeItUsers memeItUsers;

    private MemeItUsers() {

    }

    public static void init() {
        memeItUsers = new MemeItUsers();
    }

    public static MemeItUsers getInstance() {
        return memeItUsers;
    }

    public MyUser getMyUser(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return MyUser.createFromCache(sp);
    }

    /**
     * this is to get the user detail
     *
     * @param listener the Listener to be called when the action is completed
     **/
    public void getMyUserDetail(final Context context,final OnCompleteListener<User> listener) {
        MemeItClient.getInstance().getInterface()
                .getMyUser().enqueue(new MyCallBack2<User, User>(listener) {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {
                    MyUser mu = response.body().toMyUser();
                    mu.save(context);
                    Utils.checkAndFireSuccess(listener, response.body());
                } else
                    Utils.checkAndFireError(listener, OTHER_ERROR.setMessage(response.message()));
            }
        });
    }

    /**
     * this is to get user detail for a specified id
     *
     * @param uid      the id of the user to get detail for
     * @param listener the Listener to be called when the action is completed
     **/
    public void getUserDetailFor(String uid, final OnCompleteListener<User> listener) {
        MemeItClient.getInstance().getInterface()
                .getUserById(uid).enqueue(new MyCallBack<User>(listener));
    }

    /**
     * this is to get the notifications of the user
     *
     * @param skip     to skip some notifications from the retrieved list
     * @param limit    to limit the number of retrieved notifications
     * @param listener the Listener to be called when the action is completed
     **/
    public void getNotificationList(int skip, int limit, OnCompleteListener<List<Map<String,Object>>> listener) {
        MemeItClient.getInstance().getInterface()
                .getMyNotifications(skip, limit)
                .enqueue(new MyCallBack<>(listener));
    }

    /**
     * this is to get the number of unseen notification of the user
     *
     * @param listener the Listener to be called when the action is completed
     **/
    public void getNotificationCount(OnCompleteListener<Integer> listener) {
        MemeItClient.getInstance().getInterface()
                .getNotifCount().enqueue(new MyCallBack<>(listener));
    }

    /**
     * this is to get the followers list of the user
     *
     * @param skip     to skip some followers from the retrieved list
     * @param limit    to limit the number of retrieved followers list
     * @param listener the Listener to be called when the action is completed
     **/
    public void getMyFollowerList(int skip, int limit, OnCompleteListener<List<User>> listener) {
        MemeItClient.getInstance().getInterface()
                .getMyFollowersList(skip, limit)
                .enqueue(new MyCallBack<List<User>>(listener));
    }

    /**
     * this is to get the list of users the user follow
     *
     * @param skip     skips the specified amount in the list
     * @param limit    limits the number of items in the list
     * @param listener the Listener to be called when the action is completed
     **/
    public void getMyFollowingList(int skip, int limit, OnCompleteListener<List<User>> listener) {
        MemeItClient.getInstance().getInterface()
                .getMyFollowingList(skip, limit)
                .enqueue(new MyCallBack<List<User>>(listener));
    }


    /**
     * this is to get the followers list of a specified user
     *
     * @param uid      the user id to get the list for
     * @param skip     skips the specified amount in the list
     * @param limit    limits the number of items in the list
     * @param listener the Listener to be called when the action is completed
     **/
    public void getFollowerListFor(String uid, int skip, int limit, OnCompleteListener<List<User>> listener) {
        MemeItClient.getInstance().getInterface()
                .getFollowersListForUser(uid, skip, limit)
                .enqueue(new MyCallBack<List<User>>(listener));
    }

    /**
     * this is to get the list of users a specified user follow
     *
     * @param uid      the user id to get the list for
     * @param skip     skips the specified amount in the list
     * @param limit    limits the number of items in the list
     * @param listener the Listener to be called when the action is completed
     **/
    public void getFollowingListFor(String uid, int skip, int limit, OnCompleteListener<List<User>> listener) {
        Log.d("ufak", "getFollowingListFor: " + uid);
        MemeItClient.getInstance().getInterface()
                .getFollowingListForUser(uid, skip, limit)
                .enqueue(new MyCallBack<List<User>>(listener));
    }

    /**
     * this is to get the list of badges the user gained
     *
     * @param listener the Listener to be called when the action is completed
     **/
    public void getMyBadges(OnCompleteListener<List<Badge>> listener) {
        MemeItClient.getInstance().getInterface()
                .getMyBadges()
                .enqueue(new MyCallBack<List<Badge>>(listener));
    }

    /**
     * this is to get the list of badges a specified user gained
     *
     * @param uid      the user id to get the list for
     * @param listener the Listener to be called when the action is completed
     **/
    public void getBadgesFor(String uid, OnCompleteListener<List<Badge>> listener) {
        MemeItClient.getInstance().getInterface()
                .getBadgesFor(uid)
                .enqueue(new MyCallBack<List<Badge>>(listener));
    }

    public void getUserSuggestions(OnCompleteListener<List<User>> listener) {
        MemeItClient.getInstance().getInterface()
                .getUserSuggestions()
                .enqueue(new MyCallBack<List<User>>(listener));
    }

    //====================================================================================================================

    /**
     * this is to follow a user
     *
     * @param uid      the user id of ther user to follow
     * @param listener the Listener to be called when the action is completed
     **/
    public void followUser(String uid, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .followUser(uid)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    /**
     * this is to unfollow a user
     *
     * @param uid      the user id of ther user to follow
     * @param listener the Listener to be called when the action is completed
     **/
    public void unFollowUser(String uid, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .unfollowUser(uid)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    /**
     * this is to update the user data
     * i.e to update the user's name,username or profile picture
     *
     * @param user     the updated user data
     * @param listener the Listener to be called when the action is completed
     **/
    public void updateMyData(User user, final OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .uploadUserData(user)
                .enqueue(new MyCallBack<ResponseBody>(listener) {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            PrefUtils.get().edit()
                                    .putBoolean(PREFERENCE_USER_DATA_SAVED, true)
                                    .apply();
                            Utils.checkAndFireSuccess(listener, response.body());
                        } else {
                            Utils.checkAndFireError(listener, OTHER_ERROR.setMessage(response.message()));
                        }
                    }
                });
    }

    /**
     * this is to mark a specific notification seen
     *
     * @param nid      the notification id to mark seen
     * @param listener the Listener to be called when the action is completed
     **/
    public void markNotificationSeen(String nid, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .markSingleNotificationSeen(nid)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    /**
     * this is to mark all notification seen
     *
     * @param listener the Listener to be called when the action is completed
     **/
    public void markAllNotificationSeen(OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .markNotificationSeen()
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    /**
     * This is to delete the user account completely
     * Care must be taken here
     *
     * @param listener the Listener to be called when the action is completed
     */
    public void deleteMe(Context context, OnCompleteListener<ResponseBody> listener) {
        MemeItAuth.getInstance().signOut(context);
        MemeItClient.getInstance().getInterface()
                .deleteMe()
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    public void isUsernameAvailable(final String username, final OnCompleteListener<Username> listener) {
        MemeItClient.getInstance().getInterface()
                .isUsernameAvailable(username)
                .enqueue(new MyCallBack<Username>(listener));
    }

    public void updateUsername(final Context context, final String username, final OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .updateUsername(User.username(username))
                .enqueue(new MyCallBack2<ResponseBody, ResponseBody>(listener) {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            getMyUser(context).setUsername(username).save(context);
                            Utils.checkAndFireSuccess(listener, response.body());
                        } else
                            Utils.checkAndFireError(listener, OTHER_ERROR.setMessage(response.message()));
                    }
                });
    }

    public void updatePassword(String password, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .updatePassword(AuthInfo.ofPassword(password))
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    public void updateName(final Context context, final String name, final OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .updateName(User.name(name))
                .enqueue(new MyCallBack2<ResponseBody, ResponseBody>(listener) {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            getMyUser(context).setName(name).save(context);
                            Utils.checkAndFireSuccess(listener, response.body());
                        } else
                            Utils.checkAndFireError(listener, OTHER_ERROR.setMessage(response.message()));
                    }
                });
    }

    public void updateProfilePic(final Context context, final String url, final OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .updateProfilePic(User.pic(url))
                .enqueue(new MyCallBack2<ResponseBody, ResponseBody>(listener) {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            getMyUser(context).setPic(url).save(context);
                            Utils.checkAndFireSuccess(listener, response.body());
                        } else
                            Utils.checkAndFireError(listener, OTHER_ERROR.setMessage(response.message()));
                    }
                });
    }

    public void updateCoverPic(String url, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .updateCoverPic(User.cpic(url))
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    public void setFollowingTags(String[] tags, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .setFollowingTags(tags)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    public void followTags(String[] tags, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .followTags(tags)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    public void unFollowTags(String tag, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .unfollowTag(tag)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    public void getFollowingTags(OnCompleteListener<List<Tag>> listener) {
        MemeItClient.getInstance().getInterface()
                .getMyTags()
                .enqueue(new MyCallBack<List<Tag>>(listener));
    }

}
