package com.memeit.backend;

import com.memeit.backend.dataclasses.Comment;
import com.memeit.backend.dataclasses.Meme;
import com.memeit.backend.dataclasses.Reaction;
import com.memeit.backend.dataclasses.Tag;
import com.memeit.backend.utilis.MyCallBack;
import com.memeit.backend.utilis.OnCompleteListener;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.memeit.backend.utilis.Utils.checkAndFireSuccess;

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


    public void getRefreshedMeme(final Meme meme, final OnCompleteListener<Meme> listener) {
        MemeItClient.getInstance().getInterface()
                .getRefreshedMeme(meme.getMemeId())
                .enqueue(new MyCallBack<Meme>(listener) {
                    @Override
                    public void onResponse(Call<Meme> call, Response<Meme> response) {
                        if (response.isSuccessful()) {
                            checkAndFireSuccess(this.listener,meme.refresh(response.body()));
                        } else
                            super.onResponse(call, response);
                    }
                });
    }

    /**
     * this is to get home page meme list for a logged in user
     *
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getHomeMemes(int skip, int limit, OnCompleteListener<List<Meme>> listener) {
        MemeItClient.getInstance().getInterface()
                .getHomeMemes(skip, limit)
                .enqueue(new MyCallBack<List<Meme>>(listener));
    }

    /**
     * this is to get home page meme list for a not logged in user
     *
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getHomeMemesGuest(int skip, int limit, OnCompleteListener<List<Meme>> listener) {
        MemeItClient.getInstance().getInterface()
                .getHomeMemesForGuest(skip, limit)
                .enqueue(new MyCallBack<List<Meme>>(listener));
    }

    /**
     * this is to get the trending meme list
     *
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getTrendingMemes(int skip, int limit, OnCompleteListener<List<Meme>> listener) {
        MemeItClient.getInstance().getInterface()
                .getTrendingMemes(skip, limit)
                .enqueue(new MyCallBack<List<Meme>>(listener));

    }

    /**
     * this is to get the favourite memes of the user
     *
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getFavouriteMemes(int skip, int limit, OnCompleteListener<List<Meme>> listener) {
        MemeItClient.getInstance().getInterface()
                .getFavouriteMemes(skip, limit)
                .enqueue(new MyCallBack<List<Meme>>(listener));
    }

    /**
     * this is to get the favourite memes of a specified user
     *
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getFavouriteMemesFor(String uid, int skip, int limit, OnCompleteListener<List<Meme>> listener) {
        MemeItClient.getInstance().getInterface()
                .getFavouriteMemesFor(uid, skip, limit)
                .enqueue(new MyCallBack<List<Meme>>(listener));
    }

    /**
     * this is to get the posted memes of the user
     *
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getMyMemes(int skip, int limit, OnCompleteListener<List<Meme>> listener) {
        MemeItClient.getInstance().getInterface()
                .getMyMemes(skip, limit)
                .enqueue(new MyCallBack<List<Meme>>(listener));
    }

    /**
     * this is to get the favourite memes of a specified user
     *
     * @param uid      the User Id to retrieve the memes for
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getMemesOf(String uid, int skip, int limit, OnCompleteListener<List<Meme>> listener) {
        MemeItClient.getInstance().getInterface()
                .getMemesFor(uid, skip, limit)
                .enqueue(new MyCallBack<List<Meme>>(listener));
    }

    /**
     * this is to get meme list that match the search query
     *
     * @param text   the search text (can be null to search only by tags)
     * @param tags   the search tags (can be null to search only by text)
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getFileterdMemes(String text,String tags[], int skip, int limit, OnCompleteListener<List<Meme>> listener) {
        MemeItClient.getInstance().getInterface()
                .getFilteredMemes(text,tags, skip, limit)
                .enqueue(new MyCallBack<List<Meme>>(listener));
    }
    public void getReactorsForMeme(String mid, int skip, int limit, OnCompleteListener<List<Reaction>> listener) {
        MemeItClient.getInstance().getInterface()
                .getReactorsForMeme(mid, skip, limit)
                .enqueue(new MyCallBack<>(listener));
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
    public void postMeme(Meme meme, OnCompleteListener<Meme> listener) {
        MemeItClient.getInstance().getInterface()
                .postMeme(meme)
                .enqueue(new MyCallBack<Meme>(listener));
    }

    /**
     * this is to edit a meme thats been posted
     *
     * @param meme     the edited meme
     * @param listener the Listener to be called when the action is completed
     **/
    public void editMeme(Meme meme, OnCompleteListener<Meme> listener) {
        MemeItClient.getInstance().getInterface()
                .updateMeme(meme)
                .enqueue(new MyCallBack<Meme>(listener));
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
     * this is to react to a meme.
     * A user can only have one reaction for a meme,
     * reacting again to the same meme results to updating
     * the previous meme
     *
     * @param reaction the reaction object
     * @param listener the Listener to be called when the action is completed
     **/
    public void reactToMeme(Reaction reaction, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .reactToMeme(reaction)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    /**
     * this is delete users reaction to a meme
     *
     * @param mid      the meme id to unreact to
     * @param listener the Listener to be called when the action is completed
     **/
    public void unreactToMeme(String mid, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .unreactToMeme(mid)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    /**
     * this is to comment on memes
     *
     * @param comment  the comment object to be commented
     * @param listener the Listener to be called when the action is completed
     **/
    public void comment(Comment comment, OnCompleteListener<Comment> listener) {
        MemeItClient.getInstance().getInterface()
                .postComment(comment)
                .enqueue(new MyCallBack<Comment>(listener));
    }

    public void likeComment(String commentID,OnCompleteListener<ResponseBody> listener){
        MemeItClient.getInstance().getInterface()
                .likeComment(commentID)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }
    public void dislikeComment(String commentID,OnCompleteListener<ResponseBody> listener){
        MemeItClient.getInstance().getInterface()
                .dislikeComment(commentID)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }
    public void removeLikeComment(String commentID,OnCompleteListener<ResponseBody> listener){
        MemeItClient.getInstance().getInterface()
                .removeLikeComment(commentID)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }
    public void removeDislikeComment(String commentID,OnCompleteListener<ResponseBody> listener){
        MemeItClient.getInstance().getInterface()
                .removeDislikeComment(commentID)
                .enqueue(new MyCallBack<ResponseBody>(listener));
    }

    /**
     * this is to edit a comment
     *
     * @param cid           the comment id to be edited
     * @param editedComment the edited comment text
     * @param listener      the Listener to be called when the action is completed
     **/
    public void editComment(String cid, String editedComment, OnCompleteListener<ResponseBody> listener) {
        MemeItClient.getInstance().getInterface()
                .updateComment(Comment.createCommentForUpdate(cid, editedComment))
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
                .deleteComment(Comment.createCommentForDelete(mid, cid))
                .enqueue(new MyCallBack<ResponseBody>(listener));

    }

    /**
     * this is to get list of popular tags(used more often)
     *
     * @param text   the search text .Only for filtering the tags(can be null)
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/
    public void getPopularTags(String text, int skip, int limit, OnCompleteListener<List<Tag>> listener) {
        MemeItClient.getInstance().getInterface()
                .getPopularTags(text, skip, limit)
                .enqueue(new MyCallBack<List<Tag>>(listener));
    }
    /**
     * this is to get list of trending tags(recent most used)
     *
     * @param skip     to skip some memes from the retrieved list
     * @param limit    to limit the number of retrieved memes
     * @param listener the Listener to be called when the action is completed
     **/

    public void getTrendingTags(int skip, int limit, OnCompleteListener<List<Tag>> listener) {
        MemeItClient.getInstance().getInterface()
                .getTrendingTags(skip, limit)
                .enqueue(new MyCallBack<>(listener));
    }
    public void getSuggestedTags(int skip, int limit, OnCompleteListener<List<Tag>> listener) {
        MemeItClient.getInstance().getInterface()
                .getSuggestedTags(skip, limit)
                .enqueue(new MyCallBack<>(listener));
    }

}
