export interface User {
  id?: number;
  email: string;
  role?: string;
}

export interface SystemHealth {
  status?: string;
  redis?: string;
}

export interface KnowledgeDocument {
  id: number | string;
  title: string;
  content?: string;
  category?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Customer {
  id: number | string;
  name?: string;
  email?: string;
  phone?: string;
  totalConversations?: number;
  createdAt?: string;
}

export interface ConversationMessage {
  id?: number | string;
  role?: string;
  sender?: string;
  content?: string;
  message?: string;
  createdAt?: string;
  timestamp?: string;
  toolName?: string;
  toolResults?: ToolResult[];
}

export interface ToolResult {
  id?: number | string;
  conversationId?: number | string;
  toolName?: string;
  name?: string;
  status?: string;
  inputJson?: string;
  outputJson?: string;
  result?: unknown;
  output?: unknown;
}

export interface Conversation {
  id: number | string;
  customerId?: number | string;
  customerName?: string;
  status?: string;
  customer?: Customer;
  messages?: ConversationMessage[];
  transcript?: ConversationMessage[];
  toolResults?: ToolResult[];
  createdAt?: string;
}

export interface Ticket {
  id: number | string;
  subject?: string;
  description?: string;
  status?: string;
  priority?: string;
  customer?: Customer;
  customerId?: number | string;
  conversationId?: number | string;
  createdAt?: string;
}

export interface Escalation {
  id: number | string;
  reason?: string;
  context?: string;
  status?: string;
  conversation?: Conversation;
  customer?: Customer;
  customerId?: number | string;
  conversationId?: number | string;
  createdAt?: string;
}

export interface AnalyticsSummary {
  conversations?: number;
  openTickets?: number;
  escalations?: number;
  toolExecutions?: number;
  customers?: number;
  knowledgeDocuments?: number;
  [key: string]: unknown;
}

export function asArray<T>(value: unknown): T[] {
  if (Array.isArray(value)) return value as T[];
  if (value && typeof value === 'object') {
    const object = value as Record<string, unknown>;
    for (const key of ['content', 'items', 'results', 'data']) {
      if (Array.isArray(object[key])) return object[key] as T[];
    }
  }
  return [];
}
