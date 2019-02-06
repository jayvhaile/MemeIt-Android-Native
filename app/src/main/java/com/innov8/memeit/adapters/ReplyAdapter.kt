package com.innov8.memeit.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.afollestad.materialdialogs.MaterialDialog
import com.innov8.memeit.R
import com.innov8.memeit.activities.ProfileActivity
import com.innov8.memeit.commons.MyViewHolder
import com.innov8.memeit.commons.SimpleELEListAdapter
import com.innov8.memeit.commons.views.MemeItTextView
import com.innov8.memeit.commons.views.ProfileDraweeView
import com.innov8.memeit.utils.*
import com.memeit.backend.MemeItClient
import com.memeit.backend.MemeItMemes
import com.memeit.backend.call
import com.memeit.backend.models.Reply

class ReplyAdapter(context: Context, val commentID: String) : SimpleELEListAdapter<Reply>(context, R.layout.list_item_reply) {
    override fun createViewHolder(view: View): MyViewHolder<Reply> {
        return ReplyViewHolder(view)
    }


    override var emptyDrawableId: Int = com.innov8.memeit.R.drawable.no_comments
    override var errorDrawableId: Int = com.innov8.memeit.R.drawable.ic_no_internet
    override var emptyDescription: String = "No replies yet."
    override var errorDescription: String = "Couldn't load replies"
    override var emptyActionText: String? = ""
    override var errorActionText: String? = "Try Again"


    internal val linkActions by lazy {
        generateTextLinkActions(context)
    }

    inner class ReplyViewHolder(itemView: View) : MyViewHolder<Reply>(itemView), View.OnClickListener {
        private val replyTextV: MemeItTextView = itemView.findViewById(R.id.reply_textview)
        private val dateV: TextView = itemView.findViewById(R.id.reply_date_view)
        private val posterPicV: ProfileDraweeView = itemView.findViewById(R.id.reply_poster_pp)
        private val edit: ImageView = itemView.findViewById(R.id.edit)
        private val delete: ImageView = itemView.findViewById(R.id.delete)
        private val actionHolder: View = itemView.findViewById(R.id.reply_action_holder)


        init {
            posterPicV.setOnClickListener {
                val i = Intent(context, ProfileActivity::class.java)
                i.putExtra("user", getItemAt(adapterPosition).poster)
                context.startActivity(i)
            }
            delete.setOnClickListener(this)
            edit.setOnClickListener(this)
            replyTextV.onLinkClicked = linkActions
        }

        private fun applySpan(text: String, vararg words: String): Spannable =
                text.toSpannable().apply {
                    words.forEach {
                        val i = text.indexOf(it)
                        this[i..i + it.length] = StyleSpan(Typeface.BOLD)
                        this[i..i + it.length] = RelativeSizeSpan(1.05f)
                    }
                }

        override fun bind(t: Reply) {
            posterPicV.loadImage(t.poster!!.imageUrl)
            posterPicV.setText(t.poster!!.name.prefix())
            replyTextV.text = applySpan("${t.poster!!.name} ${t.reply}", t.poster!!.name!!)
            dateV.text = t.date!!.formateAsDate()
            actionHolder.visibleBy(MemeItClient.myUser!!.id == t.poster!!.uid)
        }


        override fun onClick(v: View) {
            val reply = getItemAt(adapterPosition)
            val pos = adapterPosition
            when (v.id) {
                R.id.delete -> MaterialDialog.Builder(context)
                        .title("Delete Reply?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive { _, _ ->
                            MemeItMemes.deleteReply(commentID, reply.id!!).call({
                                remove(reply)
                                notifyItemRemoved(pos)
                                itemView.snack("Reply Deleted.")
                            }) {
                                itemView.snack("An error has occurred. Please try again.\n$it")
                            }
                        }.show()
                R.id.edit -> MaterialDialog.Builder(context)
                        .title("Edit Reply")
                        .input("Reply", reply.reply, false, MaterialDialog.InputCallback { _, input ->
                            if (TextUtils.isEmpty(input) || reply.reply == input.toString())
                                return@InputCallback
                            val r = Reply(id = reply.id, reply = input.toString())
                            MemeItMemes.updateReply(commentID, r).call({
                                itemView.snack("Reply Edited.")
                                reply.reply = input.toString()
                                notifyItemChanged(adapterPosition)
                            }) {
                                itemView.snack("Editing has failed because of a problem. Please try again.\n$it")
                            }
                        }).show()
            }
        }

    }

}