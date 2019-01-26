import { InMemoryDbService } from 'angular-in-memory-web-api'
import { Category } from './pages/categories/shared/category.model';

export class InMemoryDataBase implements InMemoryDbService {

    createDb(){
        const categories: Category[] = [
            {id: 1, name: "Moradia", description: "Pagamentos de contas da casa"},
            {id: 2, name: "Saúde", description: "Plano de saúde e remédios"},
            {id: 3, name: "Lazer", description: "Cinema, parques, praia, etc"},
            {id: 4, name: "Salário", description: "Recebimento de salário"},
            {id: 5, name: "Freelas", description: "Trabalho como freelancer"}
        ];

        const entries: any[] = [
            {id: 1, name: "Gás", categoryId: categories[0].id, category: categories[0], paid: false, amount: "70,00", date: "10/10/2019", type: "expense"},
            {id: 2, name: "Mercado", categoryId: categories[0].id, category: categories[0], paid: false, amount: "900,00", date: "01/10/2019", type: "expense"},
            {id: 3, name: "Salário", categoryId: categories[3].id, category: categories[3], paid: true, amount: "2000,00", date: "05/01/2019", type: "renevue"},
            {id: 4, name: "Cinema", categoryId: categories[2].id, category: categories[2], paid: true, amount: "100,00", date: "05/06/2019", type: "renevue"},
            {id: 5, name: "Farmácia", categoryId: categories[1].id, category: categories[1], paid: true, amount: "120,00", date: "12/03/2019", type: "renevue"}
        ]

        return { categories, entries };
    }
}