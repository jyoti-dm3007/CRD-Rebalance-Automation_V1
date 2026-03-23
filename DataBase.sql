-- public.balance_batch_results definition

-- Drop table

-- DROP TABLE public.balance_batch_results;

CREATE TABLE public.balance_batch_results (
	batch_id int4 NOT NULL,
	sec_name varchar(20) NOT NULL,
	"action" varchar(20) NULL,
	share_quantity float8 NULL,
	calculated_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT balance_batch_results_pkey PRIMARY KEY (batch_id, sec_name)
);


-- public.securities definition

-- Drop table

-- DROP TABLE public.securities;

CREATE TABLE portfolio_securities (
    id SERIAL PRIMARY KEY,
    portfolio_id INT NOT NULL,
    sec_name VARCHAR(10) NOT NULL,
    target_percent DECIMAL(5,2) NOT NULL,
    current_percent DECIMAL(5,2) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO portfolio_securities 
(portfolio_id, sec_name, target_percent, current_percent, unit_price)
VALUES
(1, 'IBM', 20, 10, 150),
(1, 'MSFT', 20, 20, 90),
(1, 'ORCL', 20, 30, 220),
(1, 'AAPL', 20, 20, 450),
(1, 'HD', 20, 20, 70);