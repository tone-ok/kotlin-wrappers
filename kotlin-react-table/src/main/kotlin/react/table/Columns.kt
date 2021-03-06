package react.table

import kotlinext.js.jsObject
import react.Child
import react.ReactElement

fun <D> columns(
    block: ColumnBuilder<D>.() -> Unit,
): Array<out Column<D, *>> =
    ColumnBuilder<D>().apply(block).build()

class ColumnBuilder<D> {
    private val columns = mutableListOf<Column<D, *>>()

    fun <V> column(block: SimpleColumn<D, V>.() -> Unit) {
        val column = jsObject<SimpleColumn<D, V>> {
            // Convert Kotlin objects to String otherwise react-table thinks they are React Components
            cellFunction = { props -> props.value.toString().unsafeCast<Child>() }
            block()
        }
        columns.add(column)
    }

    fun <V> column(
        id: IdType<D>,
        header: String,
        accessor: D.() -> V,
    ) {
        column<V> {
            this.id = id
            this.header = header
            this.accessorFunction = accessor
        }
    }

    fun column(
        id: IdType<D>,
        header: String,
        cell: (CellProps<D, D>) -> ReactElement,
    ) {
        column<D> {
            this.id = id
            this.header = header
            this.accessorFunction = { it }
            this.cellFunction = cell
        }
    }

    fun <V> column(
        id: IdType<D>,
        header: String,
        accessor: D.() -> V,
        cell: (CellProps<D, V>) -> ReactElement,
    ) {
        column<V> {
            this.id = id
            this.header = header
            this.accessorFunction = accessor
            this.cellFunction = cell
        }
    }

    fun group(handler: ColumnGroup<D>.() -> Unit) {
        columns.add(jsObject(handler))
    }

    fun group(
        id: IdType<D>,
        header: String,
        columns: ColumnBuilder<D>.() -> Unit,
    ) {
        group {
            this.id = id
            this.header = header
            this.columns = columns(columns)
        }
    }

    fun build(): Array<out Column<D, *>> =
        columns.toTypedArray()
}
