import client from '../api/client'

export async function downloadFile(url, filename, params = {}) {
  const response = await client.get(url, { params, responseType: 'blob' })
  const blob = new Blob([response.data], { type: response.headers['content-type'] || 'application/octet-stream' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = filename
  document.body.appendChild(link)
  link.click()
  link.remove()
  URL.revokeObjectURL(link.href)
}
