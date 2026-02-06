import axios, { AxiosInstance, AxiosResponse } from 'axios'

interface ZeusApiResponse<T> {
  statusCode: number
  messageText: string
  responseData: T
}

class HttpConnectionManager {
  private axiosInstance: AxiosInstance
  
  constructor() {
    this.axiosInstance = axios.create({
      baseURL: '/zeus-api',
      timeout: 15000,
      headers: {
        'Content-Type': 'application/json'
      }
    })
    
    this.setupRequestInterceptor()
    this.setupResponseInterceptor()
  }
  
  private setupRequestInterceptor(): void {
    this.axiosInstance.interceptors.request.use(
      (configuration) => {
        const authToken = localStorage.getItem('zeus_auth_token')
        if (authToken) {
          configuration.headers['zeus-auth-token'] = authToken
        }
        return configuration
      },
      (error) => Promise.reject(error)
    )
  }
  
  private setupResponseInterceptor(): void {
    this.axiosInstance.interceptors.response.use(
      (response: AxiosResponse<ZeusApiResponse<any>>) => {
        const { statusCode, messageText, responseData } = response.data
        
        if (statusCode === 200) {
          return responseData
        } else {
          this.displayErrorNotification(messageText)
          return Promise.reject(new Error(messageText))
        }
      },
      (error) => {
        const errorMessage = error.response?.data?.messageText || '网络连接失败'
        this.displayErrorNotification(errorMessage)
        return Promise.reject(error)
      }
    )
  }
  
  private displayErrorNotification(message: string): void {
    console.error('API Error:', message)
  }
  
  public performGetRequest<T>(urlPath: string, queryParams?: any): Promise<T> {
    return this.axiosInstance.get(urlPath, { params: queryParams })
  }
  
  public performPostRequest<T>(urlPath: string, payloadData?: any): Promise<T> {
    return this.axiosInstance.post(urlPath, payloadData)
  }
  
  public performPutRequest<T>(urlPath: string, payloadData?: any): Promise<T> {
    return this.axiosInstance.put(urlPath, payloadData)
  }
  
  public performDeleteRequest<T>(urlPath: string): Promise<T> {
    return this.axiosInstance.delete(urlPath)
  }
}

export const httpManager = new HttpConnectionManager()
