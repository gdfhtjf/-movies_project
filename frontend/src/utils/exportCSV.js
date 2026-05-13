/**
 * CSV 导出工具
 * 将表格数据导出为 UTF-8 BOM 编码的 CSV 文件，兼容中文 Excel 直接打开
 *
 * @param {string} filename - 导出文件名（不含扩展名）
 * @param {Array<{key:string, title:string}>} columns - 列定义，key 为数据字段，title 为CSV表头
 * @param {Array<Object>} rows - 数据行数组
 *
 * @example
 * exportToCSV('电影列表', [
 *   { key: 'title', title: '电影名称' },
 *   { key: 'price', title: '价格' },
 * ], movies)
 *
 * @performance 支持 1000+ 行数据导出，使用 Blob + ObjectURL 避免 DOM 操作
 */
export function exportToCSV(filename, columns, rows) {
  const headers = columns.map((c) => c.title || c.key).join(',')
  const body = rows
    .map((row) =>
      columns
        .map((col) => {
          let val = row[col.key]
          if (val == null) return ''
          val = String(val).replace(/"/g, '""')
          return /[",\n\r]/.test(val) ? `"${val}"` : val
        })
        .join(',')
    )
    .join('\n')

  const BOM = '\uFEFF'
  const blob = new Blob([BOM + headers + '\n' + body], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `${filename}_${new Date().toISOString().slice(0, 10)}.csv`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}
