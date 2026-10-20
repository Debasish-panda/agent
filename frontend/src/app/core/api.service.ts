import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AnalyticsSummary, Conversation, Customer, Escalation, KnowledgeDocument, SystemHealth, Ticket, User
} from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api';

  login(email: string, password: string): Observable<{ token: string; user: User }> {
    return this.http.post<{ token: string; user: User }>(`${this.baseUrl}/auth/login`, { email, password });
  }

  me(): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/auth/me`);
  }

  health(): Observable<SystemHealth> {
    return this.http.get<SystemHealth>(`${this.baseUrl}/system/health`);
  }

  summary(): Observable<AnalyticsSummary> {
    return this.http.get<AnalyticsSummary>(`${this.baseUrl}/analytics/summary`);
  }

  knowledge(): Observable<KnowledgeDocument[]> {
    return this.http.get<KnowledgeDocument[]>(`${this.baseUrl}/knowledge`);
  }

  createKnowledge(document: Pick<KnowledgeDocument, 'title' | 'content'>): Observable<KnowledgeDocument> {
    return this.http.post<KnowledgeDocument>(`${this.baseUrl}/knowledge`, document);
  }

  updateKnowledge(id: number | string, document: Pick<KnowledgeDocument, 'title' | 'content'>): Observable<KnowledgeDocument> {
    return this.http.put<KnowledgeDocument>(`${this.baseUrl}/knowledge/${encodeURIComponent(id)}`, document);
  }

  deleteKnowledge(id: number | string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/knowledge/${encodeURIComponent(id)}`);
  }

  searchKnowledge(query: string): Observable<unknown> {
    return this.http.post(`${this.baseUrl}/knowledge/search`, { query });
  }

  customers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.baseUrl}/customers`);
  }

  conversations(): Observable<Conversation[]> {
    return this.http.get<Conversation[]>(`${this.baseUrl}/conversations`);
  }

  createConversation(payload: {
    customerId?: string | number;
    customerName?: string;
    customerEmail?: string;
    customerPhone?: string;
    channel?: string;
    message: string;
  }): Observable<Conversation> {
    return this.http.post<Conversation>(`${this.baseUrl}/conversations`, payload);
  }

  conversation(id: number | string): Observable<Conversation> {
    return this.http.get<Conversation>(`${this.baseUrl}/conversations/${encodeURIComponent(id)}`);
  }

  addConversationMessage(id: number | string, content: string): Observable<Conversation> {
    return this.http.post<Conversation>(`${this.baseUrl}/conversations/${encodeURIComponent(id)}/messages`, { content });
  }

  toolExecutions(): Observable<unknown> {
    return this.http.get(`${this.baseUrl}/tools/executions`);
  }

  tickets(status?: string): Observable<Ticket[]> {
    const params = status ? new HttpParams().set('status', status) : undefined;
    return this.http.get<Ticket[]>(`${this.baseUrl}/tickets`, { params });
  }

  escalations(): Observable<Escalation[]> {
    return this.http.get<Escalation[]>(`${this.baseUrl}/escalations`);
  }
}
