package com.memeit.backend;
import com.memeit.backend.dataclasses.Comment;
import com.memeit.backend.dataclasses.MemeRequest;
import com.memeit.backend.dataclasses.MemeResponse;
import com.memeit.backend.utilis.MyCallBack;
import com.memeit.backend.utilis.OnCompleteListener;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * This Class Provide the interface to get memes, post memes to the server and many other actions
 * related to memes.
 **/
public class MemeItMemes {
    private static MemeItMemes memeItmemes;

    private MemeItMemes() {

    }

    public static void init() {
        memeItmemes = new MemeItMemes();
    }

    public static MemeItMemes getInstance() {
        return memeItmemes;
    }
    /**
     * this is to get home page meme list for a logged in user
     *
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getHomeMemes(int skip, int limit, OnCompleteListener<List<MemeResponse>> listener) {
        MemeItClient.getInstance().getInterface()
                .getHomeMemes(skip, limit)
                .enqueue(new MyCallBack<List<MemeResponse>>(listener));
    }

    /**
     * this is to get home page meme list for a not logged in user
     *
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getHomeMemesGuest(int skip, int limit, OnCompleteListener<List<MemeResponse>> listener) {
        MemeItClient.getInstance().getInterface()
                .getHomeMemesForGuest(skip, limit)
                .enqueue(new MyCallBack<List<MemeResponse>>(listener));
    }

    /**
     * this is to get the trending meme list
     *
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getTrendingMemes(int skip, int limit, OnCompleteListener<List<MemeResponse>> listener) {
       MemeItClient.getInstance().getInterface()
                .getTrendingMemes(skip, limit)
                .enqueue(new MyCallBack<List<MemeResponse>>(listener));

    }

    /**
     * this is to get the favourite memes of the user
     *
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getFavouriteMemes(int skip, int limit, OnCompleteListener<List<MemeResponse>> listener) {
        MemeItClient.getInstance().getInterface()
                .getFavouriteMemes(skip, limit)
                .enqueue(new MyCallBack<List<MemeResponse>>(listener));
    }

    /**
     * this is to get the favourite memes of a specified user
     *
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getFavouriteMemesFor(String uid, int skip, int limit, OnCompleteListener<List<MemeResponse>> listener) {
        MemeItClient.getInstance().getInterface()
                .getFavouriteMemesFor(uid, skip, limit)
                .enqueue(new MyCallBack<List<MemeResponse>>(listener));
    }

    /**
     * this is to get the posted memes of the user
     *
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getMyMemes(int skip, int limit, OnCompleteListener<List<MemeResponse>> listener) {
        MemeItClient.getInstance().getInterface()
                .getMyMemes(skip, limit)
                .enqueue(new MyCallBack<List<MemeResponse>>(listener));
    }

    /**
     * this is to get the favourite memes of a specified user
     *
     * @param uid      the User Id to retrieve the memes for
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getMemesOf(String uid, int skip, int limit, OnCompleteListener<List<MemeResponse>> listener) {
        MemeItClient.getInstance().getInterface()
                .getMemesFor(uid, skip, limit)
                .enqueue(new MyCallBack<List<MemeResponse>>(listener));
    }

    /**
     * this is to get meme list that match the search query
     *
     * @param query    the search query
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getFileterdMemes(String query, int skip, int limit, OnCompleteListener<List<MemeResponse>> listener) {
        MemeItClient.getInstance().getInterface()
                .getFilteredMemes(query, skip, limit)
                .enqueue(new MyCallBack<List<MemeResponse>>(listener));
    }

    public void getCommentsForMeme(String mid, int skip, int limit, OnCompleteListener<List<Comment>> listener) {
        MemeItClient.getInstance().getInterface()
                .getCommentForMeme(mid, skip, limit)
                .enqueue(new MyCallBack<List<Comment>>(listener));
    }


    /**
     * this is to post a meme to the server
     *
     * @param meme     the meme to be posted
     * @param listener the Listener to be called when the action is completed
     **/
    public void postMeme(MemeRequest meme, OnCompleteListener<MemeResponse> listener) {
        MemeItClient.getInstance().getInterface()
                .postMeme(meme)
                .enqueue(new MyCallBack<MemeResponse>(listener));
    }

    /**
     * this is to edit a meme thats been posted
     *
     * @param mid      the meme id to be edited
     * @param meme     the edited meme
     * @param listener the Listener to be called when the action is completed
     **/
    public void editMeme(String mid, MemeResponse meme, OnCompleteListener<MemeResponse> listener) {
        MemeItClient.getInstance().getInterface()
                .updateMeme(mid, meme)
                .enqueue(new MyCallBack<MemeResponse>(listener));
    }

    /**
     * this is to delete a meme
     *
     * @param mid      the meme id to be deleted
     * @param listener the Listener to be called when the action is completed
     **/
    public void deleteMeme(String mid, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .deleteMeme(mid)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    /**
     * this is to add a meme to favourite list
     *
     * @param mid      the meme id to be added to favourite list
     * @param listener the Listener to be called when the action is completed
     **/
    public void addToFavourites(String mid, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .addMemeToFavourite(mid)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    /**
     * this is to remove a meme from favourite list
     *
     * @param mid      the meme id to be removed from favourite list
     * @param listener the Listener to be called when the action is completed
     **/
    public void removeFromFavourites(String mid, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .removeMemeFromFavourite(mid)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    /**
     * this is to like a meme
     *
     * @param mid      the meme id to be liked
     * @param listener the Listener to be called when the action is completed
     **/
    public void likeMeme(String mid, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .likeMeme(mid)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    /**
     * this is to unlike a meme
     *
     * @param mid      the meme id to be unliked
     * @param listener the Listener to be called when the action is completed
     **/
    public void unLikeMeme(String mid, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .unlikeMeme(mid)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    /**
     * this is to comment on memes
     *
     * @param mid      the meme id to be commented on
     * @param comment  the comment object to be commented
     * @param listener the Listener to be called when the action is completed
     **/
    public void comment(String mid, Comment comment, OnCompleteListener<Comment> listener) {
        MemeItClient.getInstance().getInterface()
                .postComment(comment, mid)
                .enqueue(new MyCallBack<Comment>(listener));
    }

    /**
     * this is to edit a comment
     *
     * @param mid      the meme id of the comment
     * @param cid      the comment id to be be edited on
     * @param comment  the edited comment object
     * @param listener the Listener to be called when the action is completed
     **/
    public void editComment(String mid, String cid, Comment comment, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .updateComment(comment, mid, cid)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    /**
     * this is to delete a comment
     *
     * @param mid      the meme id of the comment to be deleted
     * @param cid      the comment id to be deleted
     * @param listener the Listener to be called when the action is completed
     **/
    public void deleteComment(String mid, String cid, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .deleteComment(cid, mid)
                .enqueue(new MyCallBack<ResponseBody>(listener));

    }


}
