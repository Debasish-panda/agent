import { CommonModule, DatePipe, JsonPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { ApiService } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import {
  AnalyticsSummary, asArray, Conversation, ConversationMessage, Customer, Escalation, KnowledgeDocument, Ticket, ToolResult
} from '../core/models';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="login-page">
      <div class="login-art">
        <a class="brand login-brand" routerLink="/login"><span class="brand-mark"><span class="material-icons">graphic_eq</span></span><span>relay<span class="brand-dot">.</span><small>VOICE PLATFORM</small></span></a>
        <div class="art-content">
          <span class="eyebrow"><i></i> AI customer experience, reimagined</span>
          <h1>Every conversation<br>moves you <em>forward.</em></h1>
          <p>One intelligent workspace to deliver effortless customer support, at any scale.</p>
          <div class="art-quote"><span class="material-icons">format_quote</span><div>We’re not just answering calls. We’re building relationships, one conversation at a time.</div><small>RELAY CUSTOMER EXPERIENCE</small></div>
        </div>
        <div class="login-art-footer">BUILT FOR THE WAY PEOPLE CONNECT <span>01 — 04</span></div>
      </div>
      <div class="login-panel">
        <div class="login-form-wrap">
          <div class="mobile-brand brand"><span class="brand-mark"><span class="material-icons">graphic_eq</span></span><span>relay<span class="brand-dot">.</span></span></div>
          <span class="form-eyebrow">WELCOME BACK</span>
          <h2>Sign in to Relay</h2>
          <p class="muted">Your customer conversations are waiting.</p>
          <form (ngSubmit)="submit()">
            <label for="email">Work email</label>
            <div class="input-wrap"><span class="material-icons">mail_outline</span><input id="email" name="email" type="email" [(ngModel)]="email" placeholder="you@company.com" autocomplete="username" required></div>
            <label for="password">Password</label>
            <div class="input-wrap"><span class="material-icons">lock_outline</span><input id="password" name="password" [type]="showPassword ? 'text' : 'password'" [(ngModel)]="password" placeholder="Enter your password" autocomplete="current-password" required><button type="button" class="reveal" (click)="showPassword = !showPassword"><span class="material-icons">{{ showPassword ? 'visibility_off' : 'visibility' }}</span></button></div>
            @if (error) { <p class="error-text">{{ error }}</p> }
            <button class="button button-primary login-submit" type="submit" [disabled]="loading">{{ loading ? 'Signing in…' : 'Sign in' }}<span class="material-icons">arrow_forward</span></button>
          </form>
          <div class="demo-hint"><span class="material-icons">info_outline</span><span><b>Demo workspace</b><br>admin&#64;democommerce.com &nbsp;·&nbsp; DemoPass123!</span></div>
          <p class="login-legal">By signing in, you agree to our <a href="mailto:support@relay.local">Terms of Service</a> and <a href="mailto:support@relay.local">Privacy Policy</a>.</p>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  email = 'admin@democommerce.com';
  password = 'DemoPass123!';
  loading = false;
  error = '';
  showPassword = false;

  submit(): void {
    if (this.loading) return;
    this.loading = true;
    this.error = '';
    this.auth.login(this.email, this.password).pipe(finalize(() => this.loading = false)).subscribe({
      next: () => void this.router.navigate(['/']),
      error: error => this.error = this.message(error, 'Unable to sign in. Check your credentials and try again.')
    });
  }

  private message(error: unknown, fallback: string): string {
    return error instanceof HttpErrorResponse ? (error.error?.message || fallback) : fallback;
  }
}

@Component({
  standalone: true,
  imports: [CommonModule, DatePipe, RouterLink],
  template: `
    <div class="page-heading">
      <div><span class="eyebrow">{{ today | date:'EEEE, MMMM d' | uppercase }}</span><h1>Good to see you<span class="heading-dot">.</span></h1><p class="muted">Here’s what’s happening across your customer experience.</p></div>
      <a class="button button-primary" routerLink="/simulator"><span class="material-icons">play_circle</span> Start a test call</a>
    </div>
    @if (error) { <div class="alert error-alert">{{ error }} <button (click)="load()">Retry</button></div> }
    @if (loading) { <div class="loading-state"><span class="spinner"></span> Loading workspace analytics…</div> }
    @if (!loading && summary) {
      <div class="metric-grid">
        <article class="metric-card"><div class="metric-top"><span>Total conversations</span><span class="metric-icon indigo"><span class="material-icons">forum</span></span></div><strong>{{ value(summary.conversations) }}</strong><small>Conversations handled</small></article>
        <article class="metric-card"><div class="metric-top"><span>Customers</span><span class="metric-icon mint"><span class="material-icons">groups</span></span></div><strong>{{ value(summary.customers) }}</strong><small>In your workspace</small></article>
        <article class="metric-card"><div class="metric-top"><span>Open tickets</span><span class="metric-icon amber"><span class="material-icons">confirmation_number</span></span></div><strong>{{ value(summary.openTickets) }}</strong><small>Needs customer follow-up</small></article>
        <article class="metric-card"><div class="metric-top"><span>Escalations</span><span class="metric-icon violet"><span class="material-icons">support_agent</span></span></div><strong>{{ value(summary.escalations) }}</strong><small>Handed off to a human</small></article>
      </div>
      <div class="dashboard-grid">
        <article class="panel recent-panel">
          <div class="panel-heading"><div><h2>Recent conversations</h2><p>Latest customer interactions</p></div><a routerLink="/simulator" class="text-link">Open simulator <span class="material-icons">arrow_forward</span></a></div>
          @if (conversations.length) {
            <div class="table-scroll"><table><thead><tr><th>CONVERSATION</th><th>CUSTOMER</th><th>STATUS</th><th>STARTED</th></tr></thead><tbody>
              @for (item of conversations; track item.id) { <tr><td><b>#{{ item.id }}</b><span class="sub-cell">{{ lastMessage(item) }}</span></td><td>{{ item.customerName || 'Customer' }}</td><td><span class="status-pill" [class.good]="item.status?.toUpperCase() === 'CLOSED'">{{ item.status || 'Open' }}</span></td><td>{{ item.createdAt | date:'short' }}</td></tr> }
            </tbody></table></div>
          } @else { <div class="empty-inline"><span class="material-icons">forum</span><b>No conversations yet</b><span>Start a test call to see your first interaction here.</span><a routerLink="/simulator" class="text-link">Try the call simulator →</a></div> }
        </article>
        <div class="stack-panels">
          <article class="panel quick-panel"><div class="panel-heading"><div><h2>Workspace pulse</h2><p>Live operational snapshot</p></div><span class="status-pulse"></span></div>
            <div class="pulse-row"><span><i class="pulse-icon blue"><span class="material-icons">extension</span></i>Tool executions</span><b>{{ value(summary.toolExecutions) }}</b></div>
            <div class="pulse-row"><span><i class="pulse-icon orange"><span class="material-icons">library_books</span></i>Knowledge documents</span><b>{{ value(summary.knowledgeDocuments) }}</b></div>
            <div class="pulse-row"><span><i class="pulse-icon purple"><span class="material-icons">forum</span></i>Conversation activity</span><b>{{ value(conversations.length) }}</b></div>
          </article>
          <article class="callout-card"><span class="material-icons">auto_awesome</span><div><b>Meet your AI agent</b><p>Try a conversation and watch Relay resolve a customer request in real time.</p><a routerLink="/simulator">Launch simulator <span>↗</span></a></div></article>
        </div>
      </div>
    }
  `
})
export class DashboardComponent implements OnInit {
  private readonly api = inject(ApiService);
  summary: AnalyticsSummary | null = null;
  conversations: Conversation[] = [];
  error = '';
  loading = true;
  today = new Date();
  ngOnInit(): void { this.load(); }
  load(): void {
    this.loading = true;
    this.error = '';
    this.api.summary().subscribe({ next: data => { this.summary = data; this.loading = false; }, error: error => { this.error = errorText(error); this.loading = false; } });
    this.api.conversations().subscribe({ next: value => this.conversations = asArray<Conversation>(value), error: () => this.conversations = [] });
  }
  value(number: unknown): string | number { return typeof number === 'number' ? number.toLocaleString() : '—'; }
  percent(number: unknown): string { return typeof number === 'number' ? `${number <= 1 ? Math.round(number * 100) : number}%` : '—'; }
  duration(number: unknown): string { return typeof number === 'number' ? `${number.toFixed(number < 10 ? 1 : 0)}s` : '—'; }
  lastMessage(item: Conversation): string { const messages = item.messages ?? item.transcript ?? []; return messages.length ? (messages[messages.length - 1].content || messages[messages.length - 1].message || 'Conversation activity') : 'Conversation activity'; }
}

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, JsonPipe],
  template: `
    <div class="page-heading"><div><span class="eyebrow">LIVE TEST ENVIRONMENT</span><h1>Call simulator<span class="heading-dot">.</span></h1><p class="muted">Experience your AI agent just like a customer would.</p></div><span class="environment-badge"><i></i> MOCK TELEPHONY</span></div>
    <div class="simulator-layout">
      <section class="panel simulator-panel">
        <div class="simulator-top"><div class="agent-identity"><span class="agent-avatar"><span class="material-icons">graphic_eq</span></span><div><b>Relay AI Agent</b><span><i></i> Ready to take a call</span></div></div><button class="button button-quiet" (click)="reset()"><span class="material-icons">restart_alt</span> New call</button></div>
        <div class="call-setup">
          <label for="customer">Calling as</label>
          <select id="customer" [(ngModel)]="customerId"><option value="">New caller</option>@for (customer of customers; track customer.id) { <option [value]="customer.id">{{ customer.name || customer.email || customer.id }}</option> }</select>
          @if (!customerId) { <label class="caller-label" for="callerName">Name</label><input class="caller-input" id="callerName" name="callerName" [(ngModel)]="customerName" placeholder="Caller name"><label class="caller-label" for="callerEmail">Email</label><input class="caller-input" id="callerEmail" name="callerEmail" [(ngModel)]="customerEmail" type="email" placeholder="Caller email (optional)"> }
          <div class="scenario-hint"><span class="material-icons">lightbulb</span><span><b>Try a demo scenario</b><br>Ask about order <strong>ORD-10001</strong>, report a delay, or ask to speak with a person.</span></div>
        </div>
        <div class="conversation-stream" #stream>
          @if (!messages.length && !loading) { <div class="conversation-welcome"><span class="welcome-wave">✳</span><b>Your conversation starts here</b><span>Send a message to see how your AI agent responds.</span></div> }
          @for (message of messages; track $index) {
            <div class="message-row" [class.customer-message]="isCustomer(message)">
              <span class="message-avatar" [class.customer-avatar]="isCustomer(message)">{{ isCustomer(message) ? 'You' : 'AI' }}</span>
              <div class="message-body"><div class="message-meta">{{ isCustomer(message) ? 'Customer' : 'Relay AI agent' }}<small>{{ message.createdAt || message.timestamp | date:'shortTime' }}</small></div>@if (!message.toolName) { <div class="message-bubble">{{ message.content || message.message || '—' }}</div> }
                @if (message.toolName) { <div class="tool-result"><span class="material-icons">extension</span><div><b>{{ message.toolName }}</b><small>Persisted tool response</small><pre>{{ message.content }}</pre></div></div> }
              </div>
            </div>
          }
          @for (tool of toolResults; track tool.id || $index) { <div class="tool-result stream-tool"><span class="material-icons">extension</span><div><b>{{ tool.toolName || tool.name || 'Tool execution' }}</b><small>{{ tool.status || 'Completed' }}</small><pre>{{ tool.outputJson || tool.result || tool.output || tool.inputJson | json }}</pre></div></div> }
          @if (loading) { <div class="typing-indicator"><i></i><i></i><i></i><span>Relay is thinking…</span></div> }
          @if (error) { <div class="alert error-alert">{{ error }}</div> }
        </div>
        <form class="message-composer" (ngSubmit)="send()"><textarea name="message" [(ngModel)]="draft" (keydown)="onKey($event)" rows="1" placeholder="Type a message to your AI agent…" [disabled]="loading"></textarea><button type="submit" class="send-button" [disabled]="loading || !draft.trim()" aria-label="Send message"><span class="material-icons">arrow_upward</span></button></form>
        <div class="composer-footnote"><span>↵</span> Press Enter to send <span class="separator">·</span> Shift + Enter for a new line</div>
      </section>
      <aside class="simulator-side">
        <article class="panel"><div class="panel-heading"><div><h2>Session details</h2><p>Conversation context</p></div></div>
          <div class="detail-item"><small>CONVERSATION ID</small><b>{{ conversation?.id || 'Not started' }}</b></div>
          <div class="detail-item"><small>STATUS</small><span class="status-pill" [class.good]="conversation">{{ conversation?.status || 'Waiting for call' }}</span></div>
          <div class="detail-item"><small>MESSAGES</small><b>{{ messages.length }}</b></div>
          <div class="detail-item"><small>TOOLS EXECUTED</small><b>{{ toolResults.length || messages.reduce(toolCount, 0) }}</b></div>
        </article>
        <article class="panel handoff-card"><span class="handoff-icon"><span class="material-icons">support_agent</span></span><h3>Human handoff</h3><p>Ask your AI agent to transfer you to a human to test the escalation flow and handoff context.</p><div class="handoff-state"><i></i> Available for testing</div></article>
        <a class="side-link" href="/escalations"><span class="material-icons">open_in_new</span> View escalation queue</a>
      </aside>
    </div>
  `
})
export class SimulatorComponent implements OnInit {
  private readonly api = inject(ApiService);
  customers: Customer[] = [];
  customerId: string | number = '';
  customerName = '';
  customerEmail = '';
  conversation: Conversation | null = null;
  messages: ConversationMessage[] = [];
  toolResults: ToolResult[] = [];
  draft = '';
  loading = false;
  error = '';
  ngOnInit(): void { this.api.customers().subscribe({ next: data => this.customers = asArray<Customer>(data) }); }
  send(): void {
    const message = this.draft.trim();
    if (!message || this.loading) return;
    if (!this.conversation && !this.customerId && !this.customerName.trim()) {
      this.error = 'Choose an existing customer or enter a caller name to start the call.';
      return;
    }
    this.messages = [...this.messages, { role: 'CUSTOMER', content: message, createdAt: new Date().toISOString() }];
    this.draft = '';
    this.loading = true;
    this.error = '';
    const request = this.conversation
      ? this.api.addConversationMessage(this.conversation.id, message)
      : this.api.createConversation({
          customerId: this.customerId || undefined,
          customerName: this.customerId ? undefined : this.customerName.trim(),
          customerEmail: this.customerId ? undefined : (this.customerEmail.trim() || undefined),
          message,
          channel: 'WEB'
        });
    request.pipe(finalize(() => this.loading = false)).subscribe({
      next: conversation => this.refreshPersisted(conversation),
      error: error => { this.error = errorText(error); }
    });
  }
  reset(): void { this.conversation = null; this.messages = []; this.toolResults = []; this.customerName = ''; this.customerEmail = ''; this.error = ''; }
  onKey(event: KeyboardEvent): void { if (event.key === 'Enter' && !event.shiftKey) { event.preventDefault(); this.send(); } }
  isCustomer(message: ConversationMessage): boolean { return ['CUSTOMER', 'USER'].includes((message.role || message.sender || '').toUpperCase()); }
  toolCount(_total: number, message: ConversationMessage): number { return message.toolName ? 1 : 0; }
  private applyConversation(value: Conversation): void {
    this.conversation = value;
    this.messages = value.messages || value.transcript || this.messages;
    this.toolResults = value.toolResults || [];
  }
  private refreshPersisted(value: Conversation): void {
    this.applyConversation(value);
    this.api.conversation(value.id).subscribe({
      next: persisted => {
        this.applyConversation(persisted);
        this.api.toolExecutions().subscribe({
          next: result => this.toolResults = asArray<ToolResult>(result).filter(tool => String(tool.conversationId) === String(value.id)),
          error: () => this.toolResults = []
        });
      },
      error: () => this.error = 'The conversation was saved, but its persisted transcript could not be loaded.'
    });
  }
}

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  template: `
    <div class="page-heading"><div><span class="eyebrow">YOUR TEAM’S PLAYBOOK</span><h1>Knowledge base<span class="heading-dot">.</span></h1><p class="muted">Give your AI agent the context to resolve every conversation.</p></div><button class="button button-primary" (click)="openCreate()"><span class="material-icons">add</span> Add knowledge</button></div>
    <div class="knowledge-toolbar"><div class="search-box"><span class="material-icons">search</span><input [(ngModel)]="query" (keyup.enter)="search()" placeholder="Search your knowledge base…"><button (click)="search()">Search</button></div><span class="result-count">{{ documents.length }} {{ documents.length === 1 ? 'document' : 'documents' }}</span></div>
    @if (error) { <div class="alert error-alert">{{ error }} <button (click)="load()">Retry</button></div> }
    @if (searchResults !== null) { <article class="panel search-results"><div class="panel-heading"><div><h2>Search results</h2><p>Matches from your knowledge documents</p></div><button class="button button-quiet" (click)="searchResults = null">Clear</button></div><pre>{{ searchResults | json }}</pre></article> }
    @if (creating) {
      <form class="panel knowledge-form" (ngSubmit)="save()"><h2>{{ editingId === null ? 'Add a knowledge document' : 'Edit knowledge document' }}</h2><label>Document title<input name="title" [(ngModel)]="newTitle" required placeholder="e.g. Shipping & returns policy"></label><label>Content<textarea name="content" [(ngModel)]="newContent" rows="6" required placeholder="Write the information your AI agent should know…"></textarea></label>@if (error) { <div class="error-text">{{ error }}</div> }<div class="form-actions"><button type="button" class="button button-quiet" (click)="cancelForm()">Cancel</button><button class="button button-primary" [disabled]="saving">{{ saving ? 'Saving…' : editingId === null ? 'Save document' : 'Update document' }}</button></div></form>
    }
    @if (loading) { <div class="loading-state"><span class="spinner"></span> Loading documents…</div> }
    @if (!loading && !documents.length && !creating) { <div class="empty-state panel"><span class="empty-icon"><span class="material-icons">library_books</span></span><h2>Your knowledge base is ready to grow</h2><p>Add policies, product details, and helpful answers to give your AI agent the right context.</p><button class="button button-primary" (click)="openCreate()">Add your first document</button></div> }
    @if (documents.length) { <div class="knowledge-grid">@for (doc of documents; track doc.id) { <article class="panel knowledge-card"><div class="doc-icon"><span class="material-icons">description</span></div><div class="doc-meta"><span class="category-tag">KNOWLEDGE</span><span class="doc-actions"><button class="icon-button delete-button" aria-label="Edit document" (click)="edit(doc)"><span class="material-icons">edit</span></button><button class="icon-button delete-button" aria-label="Delete document" (click)="remove(doc)"><span class="material-icons">delete_outline</span></button></span></div><h3>{{ doc.title }}</h3><p>{{ doc.content || 'No description provided.' }}</p><div class="doc-footer"><span>Added {{ doc.createdAt | date:'mediumDate' }}</span><span>{{ doc.content?.length || 0 }} characters</span></div></article> }</div> }
  `
})
export class KnowledgeComponent implements OnInit {
  private readonly api = inject(ApiService);
  documents: KnowledgeDocument[] = [];
  loading = true;
  saving = false;
  creating = false;
  error = '';
  query = '';
  searchResults: unknown = null;
  newTitle = '';
  newContent = '';
  editingId: number | string | null = null;
  ngOnInit(): void { this.load(); }
  load(): void {
    this.loading = true;
    this.error = '';
    this.api.knowledge().pipe(finalize(() => this.loading = false)).subscribe({ next: data => this.documents = asArray<KnowledgeDocument>(data), error: error => this.error = errorText(error) });
  }
  openCreate(): void { this.editingId = null; this.newTitle = ''; this.newContent = ''; this.creating = true; this.error = ''; }
  edit(document: KnowledgeDocument): void { this.editingId = document.id; this.newTitle = document.title; this.newContent = document.content || ''; this.creating = true; this.error = ''; }
  cancelForm(): void { this.creating = false; this.editingId = null; this.error = ''; }
  save(): void {
    if (!this.newTitle.trim() || !this.newContent.trim()) return;
    this.saving = true;
    const value = { title: this.newTitle.trim(), content: this.newContent.trim() };
    const request = this.editingId === null
      ? this.api.createKnowledge(value)
      : this.api.updateKnowledge(this.editingId, value);
    request.pipe(finalize(() => this.saving = false)).subscribe({
      next: () => { this.cancelForm(); this.load(); },
      error: error => this.error = errorText(error)
    });
  }
  search(): void {
    if (!this.query.trim()) { this.searchResults = null; return; }
    this.api.searchKnowledge(this.query.trim()).subscribe({ next: value => this.searchResults = value, error: error => this.error = errorText(error) });
  }
  remove(document: KnowledgeDocument): void {
    if (!confirm(`Delete “${document.title}”?`)) return;
    this.api.deleteKnowledge(document.id).subscribe({ next: () => this.load(), error: error => this.error = errorText(error) });
  }
}

@Component({
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-heading"><div><span class="eyebrow">CUSTOMER DIRECTORY</span><h1>Customers<span class="heading-dot">.</span></h1><p class="muted">People who trust you with their business.</p></div><span class="result-count">{{ customers.length }} customers</span></div>
    @if (error) { <div class="alert error-alert">{{ error }} <button (click)="load()">Retry</button></div> }
    @if (loading) { <div class="loading-state"><span class="spinner"></span> Loading customers…</div> }
    @if (!loading && customers.length) { <article class="panel data-panel"><div class="panel-heading"><div><h2>All customers</h2><p>Customer profiles across your workspace</p></div></div><div class="table-scroll"><table><thead><tr><th>CUSTOMER</th><th>EMAIL</th><th>PHONE</th><th>ADDED</th></tr></thead><tbody>@for (customer of customers; track customer.id) { <tr><td><span class="customer-name"><span class="avatar">{{ initials(customer.name || customer.email || '') }}</span><b>{{ customer.name || 'Unnamed customer' }}</b></span></td><td>{{ customer.email || '—' }}</td><td>{{ customer.phone || '—' }}</td><td>{{ customer.createdAt | date:'mediumDate' }}</td></tr> }</tbody></table></div></article> }
    @if (!loading && !customers.length) { <div class="empty-state panel"><span class="empty-icon"><span class="material-icons">groups</span></span><h2>No customers yet</h2><p>Customer profiles will appear here when conversations are received.</p></div> }
  `
})
export class CustomersComponent implements OnInit {
  private readonly api = inject(ApiService);
  customers: Customer[] = [];
  loading = true;
  error = '';
  ngOnInit(): void { this.load(); }
  load(): void { this.loading = true; this.api.customers().pipe(finalize(() => this.loading = false)).subscribe({ next: data => this.customers = asArray<Customer>(data), error: error => this.error = errorText(error) }); }
  initials(name: string): string { return name.split(/\s+/).slice(0, 2).map(part => part[0] || '').join('').toUpperCase() || 'CU'; }
}

@Component({
  standalone: true,
  imports: [CommonModule, DatePipe],
  template: `
    <div class="page-heading"><div><span class="eyebrow">CUSTOMER CARE</span><h1>Support tickets<span class="heading-dot">.</span></h1><p class="muted">Follow up on customer issues and requests.</p></div><span class="result-count">{{ tickets.length }} tickets</span></div>
    @if (error) { <div class="alert error-alert">{{ error }} <button (click)="load()">Retry</button></div> }
    @if (loading) { <div class="loading-state"><span class="spinner"></span> Loading support tickets…</div> }
    @if (!loading && tickets.length) { <article class="panel data-panel"><div class="panel-heading"><div><h2>All tickets</h2><p>Tickets created from customer conversations</p></div></div><div class="table-scroll"><table><thead><tr><th>TICKET</th><th>SUBJECT</th><th>CUSTOMER</th><th>PRIORITY</th><th>STATUS</th><th>CREATED</th></tr></thead><tbody>@for (ticket of tickets; track ticket.id) { <tr><td><b>#{{ ticket.id }}</b></td><td>{{ ticket.subject || ticket.description || 'Support request' }}</td><td>{{ ticket.customerId ? '#' + ticket.customerId : '—' }}</td><td><span class="priority-pill" [class.priority-high]="ticket.priority?.toUpperCase() === 'HIGH'">{{ ticket.priority || 'Normal' }}</span></td><td><span class="status-pill" [class.good]="ticket.status?.toUpperCase() === 'RESOLVED'">{{ ticket.status || 'Open' }}</span></td><td>{{ ticket.createdAt | date:'mediumDate' }}</td></tr> }</tbody></table></div></article> }
    @if (!loading && !tickets.length) { <div class="empty-state panel"><span class="empty-icon"><span class="material-icons">confirmation_number</span></span><h2>All clear for now</h2><p>Support tickets created by your AI agent will show up here.</p></div> }
  `
})
export class TicketsComponent implements OnInit {
  private readonly api = inject(ApiService);
  tickets: Ticket[] = [];
  loading = true;
  error = '';
  ngOnInit(): void { this.load(); }
  load(): void { this.loading = true; this.api.tickets().pipe(finalize(() => this.loading = false)).subscribe({ next: data => this.tickets = asArray<Ticket>(data), error: error => this.error = errorText(error) }); }
}

@Component({
  standalone: true,
  imports: [CommonModule, DatePipe],
  template: `
    <div class="page-heading"><div><span class="eyebrow">HUMAN SUPPORT</span><h1>Escalations<span class="heading-dot">.</span></h1><p class="muted">Every handoff, with the full customer context in one place.</p></div><span class="result-count">{{ escalations.length }} escalations</span></div>
    @if (error) { <div class="alert error-alert">{{ error }} <button (click)="load()">Retry</button></div> }
    @if (loading) { <div class="loading-state"><span class="spinner"></span> Loading escalations…</div> }
    @if (!loading && escalations.length) { <div class="escalation-list">@for (escalation of escalations; track escalation.id) { <article class="panel escalation-card"><div class="escalation-icon"><span class="material-icons">support_agent</span></div><div class="escalation-main"><div class="escalation-heading"><div><span class="eyebrow">HANDOFF #{{ escalation.id }}</span><h2>{{ escalation.reason || 'Customer requested a human agent' }}</h2></div><span class="status-pill" [class.good]="escalation.status?.toUpperCase() === 'RESOLVED'">{{ escalation.status || 'Needs attention' }}</span></div><p>{{ escalation.context || 'No additional handoff context was provided.' }}</p><div class="escalation-meta">@if (escalation.customerId) { <span><span class="material-icons">person_outline</span>Customer #{{ escalation.customerId }}</span> }<span><span class="material-icons">schedule</span>{{ escalation.createdAt | date:'medium' }}</span>@if (escalation.conversationId) { <span><span class="material-icons">forum</span>Conversation #{{ escalation.conversationId }}</span> }</div></div></article> }</div> }
    @if (!loading && !escalations.length) { <div class="empty-state panel"><span class="empty-icon"><span class="material-icons">support_agent</span></span><h2>No active escalations</h2><p>When your AI agent hands a conversation to a person, it will appear here with the context your team needs.</p></div> }
  `
})
export class EscalationsComponent implements OnInit {
  private readonly api = inject(ApiService);
  escalations: Escalation[] = [];
  loading = true;
  error = '';
  ngOnInit(): void { this.load(); }
  load(): void { this.loading = true; this.api.escalations().pipe(finalize(() => this.loading = false)).subscribe({ next: data => this.escalations = asArray<Escalation>(data), error: error => this.error = errorText(error) }); }
}

function errorText(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) return 'Could not connect to the API. Check that the backend is running and try again.';
    return error.error?.message || `Request failed (${error.status}). Please try again.`;
  }
  return 'Something went wrong. Please try again.';
}
