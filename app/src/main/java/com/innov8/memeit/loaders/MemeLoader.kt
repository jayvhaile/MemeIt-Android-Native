package com.innov8.memeit.loaders

import android.os.Parcelable
import com.innov8.memeit.commons.Loader
import com.memeit.backend.models.HomeElement

interface MemeLoader<T : HomeElement> : Loader<T>, Parcelable