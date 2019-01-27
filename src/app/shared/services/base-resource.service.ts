import { BaseResourceModel } from '../models/base-resource.model';
import {  HttpClient } from '@angular/common/http';

import { Observable, throwError } from 'rxjs';
import { map, catchError} from 'rxjs/operators';
import { Injector } from '@angular/core';

export abstract class BaseResourceService<T extends BaseResourceModel> {
    
    protected http: HttpClient;

    constructor(protected apiPath: string, protected injector: Injector){
        this.http = injector.get(HttpClient);
    }

    getAll(): Observable<T[]>{
        return this.http.get(this.apiPath).pipe(
          catchError(this.handleError),
          map(this.jsonDataToResources)
        )
      }
    
      getById(id: number): Observable<T> {
        return this.http.get(`${this.apiPath}/${id}`).pipe(
          catchError(this.handleError),
          map(this.jsonDataToResource)
        )
      }
    
      create(resource: T): Observable<T> {
        return this.http.post(this.apiPath, resource).pipe(
          catchError(this.handleError),
          map(this.jsonDataToResource)
        )
      }
    
      update(resource: T): Observable<T> {
        return this.http.put(`${this.apiPath}`, resource).pipe(
          catchError(this.handleError),
          map(() => resource)
        )
      }
    
      delete(id: number): Observable<any> {
       return this.http.delete(`${this.apiPath}/${id}`).pipe(
        catchError(this.handleError),
        map(() => null))
      }

      protected jsonDataToResources(jsonData: any[]): T[] {
        const resources: T[] = [];
        jsonData.forEach(element => resources.push(element as T))
        return resources;
      }
    
      protected jsonDataToResource(jsonData: any): T {
        return jsonData as T;
      }
    
      protected handleError(error: any): Observable<any> {
        console.log("Erro na requisição =>", error);
        return throwError(error);
      }
}