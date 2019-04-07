package com.topjohnwu.magisk.model.observer

import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.skoumal.teanity.extensions.addOnPropertyChangedCallback
import java.io.Serializable


class ListObserver<T>(vararg dependencies: ObservableList<*>, private val observer: () -> T) :
    ObservableField<T>(), Serializable {

    val value: T get() = observer()

    init {
        dependencies.forEach { _ ->
            addOnPropertyChangedCallback { notifyChange() }
        }
    }

    @Deprecated(
        message = "Use KObservableField.value syntax from code",
        replaceWith = ReplaceWith("value")
    )
    override fun get(): T {
        return value
    }

    @Deprecated(
        message = "Observer cannot be set",
        level = DeprecationLevel.HIDDEN
    )
    override fun set(newValue: T) {
    }

    override fun toString(): String {
        return "Observer(value=$value)"
    }
}
